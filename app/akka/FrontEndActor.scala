package akka

import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.contrib.pattern.ClusterSingletonProxy
import models.{QuestionDetail, User, LoginUser, Game}


class FrontEndActor extends Actor with ActorLogging {
  import akka.ClusterProtocol._

  val masterProxy = context.actorOf(ClusterSingletonProxy.props(
    singletonPath = "/user/master/active",
    role = Some("backend")),
    name = "masterProxy")



  import akka.QuizProtocol._

  val game: Game = Game.default

  var currentQuestion: Long = 0

  var waiters: List[(ActorRef, String)] = List()


  def addWaiter(mail: String, waiter: ActorRef) = {
    context watch waiter
    waiters = waiters :+(waiter, mail)
  }

  def removeWaiter(waiter: ActorRef) = {
    waiters = waiters.filterNot(_._1 == waiter)
  }

  def createPlayerActorNameFromEmail(mail: String) = {
    mail map {
      case '@' => '-'
      case '.' => '-'
      case c => c
    }
  }

  def getPlayerActorFromEmail(mail: String): Option[ActorRef] = context.child(createPlayerActorNameFromEmail(mail))


  def handleLoginMessage(loginUser: LoginUser, feedbackActor: ActorRef) = getPlayerActorFromEmail(loginUser.mail) match {
    case Some(playerActor) => playerActor forward Login(loginUser)
    case None => feedbackActor ! UnknownUser(loginUser)
  }

  def handleCreatePlayerMessage(user: User, feedbackActor: ActorRef) = getPlayerActorFromEmail(user.mail) match {
    case Some(playerActor) =>
      feedbackActor ! AlreadyCreated(user)
      println(s"user ${user} Déjà créé")
    case None =>
      val playerActor = context.actorOf(Props(new PlayerActor), createPlayerActorNameFromEmail(user.mail))
      context.watch(playerActor)
      playerActor.tell(CreatePlayer(user), feedbackActor)
  }

  def handleAskQuestion(mail: String, numQuestion: Long, feedbackActor: ActorRef) = {
    if (numQuestion != currentQuestion + 1) {
      feedbackActor ! WrongQuestion
    } else {
      addWaiter(mail, feedbackActor)
    }
  }

  def handleAnswer(mail: String, numQuestion: Long, answer: String, feedbackActor: ActorRef) = {
    if (numQuestion != currentQuestion) {
      feedbackActor ! WrongAnswerNumber
    } else {
      getPlayerActorFromEmail(mail) match {
        case Some(playerActor) => playerActor forward ComputeAnswer(numQuestion, answer, game.getQuestion(currentQuestion).good_choice)
        case None => feedbackActor ! UnknownUser(LoginUser(mail, ""))
      }
    }
  }


  def sendQuestions() = {

    val question: QuestionDetail = game.getQuestion(currentQuestion)

    /*
    val future : List[(ActorRef,Future[Long])] = waiters.map {
      (waiter,mail) =>
          getPlayerActorFromEmail(mail) match {
              case Some(playerActor) =>
                (waiter,playerActor.ask(Score).mapTo[Long])
          }
    }
    */

    waiters.foreach {
      case (waiter, mail) =>

        println(s"send ${question} to ${mail}")

        context unwatch waiter

        waiter ! Question(
          question = question.label,
          answer_1 = question.answer_1,
          answer_2 = question.answer_2,
          answer_3 = question.answer_3,
          answer_4 = question.answer_4,
          score = 0)
    }

    waiters = List()

    currentQuestion = currentQuestion + 1

    context become questionPhase

    context.system.scheduler.scheduleOnce(game.questionDuration, self, QuestionTimeout)

    println(s"switch to questionPhase n°${currentQuestion + 1}")

  }


  def initPhase: Receive = {
    case Login(loginUser) =>
      handleLoginMessage(loginUser, sender)

      context become loginPhase
      val loginTimer = context.system.scheduler.scheduleOnce(game.loginPhaseDuration, self, LoginPhaseTimeout)

      println("switch to loginPhase")

    case CreatePlayer(user) => handleCreatePlayerMessage(user, sender)
  }


  def loginPhase: Receive = {
    case Login(loginUser) => handleLoginMessage(loginUser, sender)

    case LoginPhaseTimeout =>
      println(s"LoginPhaseTimeout")
      sendQuestions()

    case AskQuestion(mail, numQuestion) => handleAskQuestion(mail, numQuestion, sender)

    case Terminated(a) => removeWaiter(a)
  }


  def questionPhase: Receive = {

    case QuestionTimeout =>
      println(s"QuestionTimeout")
      context become synchroPhase
      context.system.scheduler.scheduleOnce(game.synchroDuration, self, SynchroTimeout)
      println("switch to synchroPhase")

    case Login(loginUser) => sender ! OutOfLoginPhase

    case AskQuestion(mail, numQuestion) => handleAskQuestion(mail, numQuestion, sender)

    case Answer(mail, numQuestion, answer) => handleAnswer(mail, numQuestion, answer, sender)

    case Terminated(a) => removeWaiter(a)
  }

  def synchroPhase: Receive = {

    case SynchroTimeout =>
      println(s"SynchroTimeout")
      sendQuestions()

    case Login(loginUser) => sender ! OutOfLoginPhase

    case AskQuestion(mail, numQuestion) => handleAskQuestion(mail, numQuestion, sender)

    case Answer(mail, numQuestion, answer) => sender ! AnswerOverTimeLimit

    case Terminated(a) => removeWaiter(a)
  }


  def receive = initPhase
}
