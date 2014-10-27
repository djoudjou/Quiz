package akka


import akka.actor._
import akka.backpressure.AbstractWorkerActor

import scala.concurrent.duration._

object PlayerActor {
  def props(clusterClient: ActorRef,createPlayerExecutorProps:Props,loginPlayerExecutorProps:Props,registerInterval: FiniteDuration = 10 seconds): Props = Props(classOf[PlayerActor], clusterClient, createPlayerExecutorProps, loginPlayerExecutorProps, registerInterval)
}

class PlayerActor(clusterClient: ActorRef,createPlayerExecutorProps:Props,loginPlayerExecutorProps:Props,registerInterval: FiniteDuration) extends AbstractWorkerActor(clusterClient,registerInterval) with ActorLogging {
  import akka.QuizProtocol._

  val createPlayerExecutor = context.watch(context.actorOf(createPlayerExecutorProps, "createPlayerExecutor"))
  val loginPlayerExecutor = context.watch(context.actorOf(loginPlayerExecutorProps, "loginPlayerExecutor"))


  def doWork(workSender: ActorRef, msg: Any): Unit = {

    msg match {
      case m:CreatePlayer =>
        createPlayerExecutor.tell(m,workSender)
      case m:Login =>
        loginPlayerExecutor.tell(m,workSender)
    }

  }


  /*
    case ComputeAnswer(numQuestion, answer, expectedAnswer) =>
    {
      println(s"user %{user.firstname} answer ${answer} for question ${numQuestion}")

      if (answers.exists(a => a.numQuestion == numQuestion)) {

        sender ! AlreadySubmittedAnswer

      } else {

        val are_u_right: Boolean = expectedAnswer == answer
        val good_answer: String = expectedAnswer

        answers = answers :+ PlayerHistory(numQuestion = numQuestion, answer = answer, excepted = expectedAnswer)
        sender ! AnswerStatus(are_u_right, good_answer, score)
      }
    }
  }
  */


}
