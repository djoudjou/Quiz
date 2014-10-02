package akka

import akka.actor._
import scala.concurrent.Future
import akka.pattern.ask
import akka.util.Timeout

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import scala.language.postfixOps
import scala.concurrent.duration._

import org.joda.time.DateTime
import scala.util.Random

import models._



object QuizActors {
    import akka.QuizProtocol._

    implicit val timeout = Timeout(5 seconds)

    val md = java.security.MessageDigest.getInstance("SHA-1")
    
    /** Quiz actor system */
    val system = ActorSystem("quiz")
    val playerSupervisor = system.actorOf(Props(new PlayerSupervisor()), "PlayerSupervisor")

}


class PlayerSupervisor() extends Actor with ActorLogging {
    import akka.QuizProtocol._

    val game : Game =  Game.default

    def createPlayerActorNameFromEmail(mail:String) = {
        mail map { 
            case '@' => '-'  
            case '.' => '-'  
            case c => c
        }
    }
    
    def getPlayerActorFromEmail(mail:String):Option[ActorRef] = context.child(createPlayerActorNameFromEmail(mail))

    /* TODO
    def initGamePhase: Receive = {
        case 
    }
    */

    def handleLoginMessage(loginUser:LoginUser,feedbackActor:ActorRef) = getPlayerActorFromEmail(loginUser.mail) match {
                                                    case Some(playerActor) => playerActor forward Login(loginUser)
                                                    case None => feedbackActor ! UnknownUser(loginUser)
                                                  }
    def handleCreatePlayerMessage(user:User,feedbackActor:ActorRef) = getPlayerActorFromEmail(user.mail) match { 
                                                    case Some(playerActor) => 
                                                        feedbackActor ! AlreadyCreated(user)
                                                        log.debug(s"user ${user} Déjà créé")
                                                    case None => 
                                                        val playerActor = context.actorOf(Props(new Player(user)),createPlayerActorNameFromEmail(user.mail))
                                                        context.watch(playerActor)
                                                        feedbackActor ! UserCreated(user)
                                                        log.debug(s"user ${user} créé")
                                                  }

    def receive = {
        case Login(loginUser)  => 
          handleLoginMessage(loginUser,sender)
          //context become loginPhase
          
          //implicit val ec = context.dispatcher
          //context.system.scheduler.scheduleOnce(game.loginPhaseDuration, self, LoginPhaseTimeout)
    
        case CreatePlayer(user) => handleCreatePlayerMessage(user, sender)
    }

    def loginPhase : Receive = {
      case Login(loginUser)  => handleLoginMessage(loginUser,sender)

      case LoginPhaseTimeout => 
            //sendQuestion(1)
            //context become questionPhase
            log.debug(s"LoginPhaseTimeout")
    }

} 


class Player(user: User) extends Actor with ActorLogging {
  import akka.QuizProtocol._

  def receive = {
    case Login(loginUser)  => 
      if(loginUser.password != user.password){
        log.debug(s"mauvais password")
        sender ! WrongPassword(loginUser)
      } else {
          log.debug(s"user ${user.firstname} logged in success")
          sender ! LoggedIn(user)
          context become loggedIn
      }
  }

  def loggedIn: Receive = {
    case Login(loginUser)  => sender ! AlreadyLoggedIn(user)
    case Answer(numQuestion,answer) => log.info(s"user %{user.firstname} answer ${answer} for question ${numQuestion}")
  }


} 


