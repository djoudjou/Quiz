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

import models.User

object QuizActors {

    implicit val timeout = Timeout(5 seconds)

    val md = java.security.MessageDigest.getInstance("SHA-1")
    
    /** Quiz actor system */
    val system = ActorSystem("quiz")
    //val supervisor = system.actorOf(Props(new Supervisor()), "PlayerSupervisor")
    
    case class GetUser()
    case class Login(userToken:String)
    case class AskQuestion(numQuestion:Long)
    case class Question(question:String,answer_1:String,answer_2:String,answer_3:String,answer_4:String)
    case class Answer(numQuestion:Long,answer:String)
    
    case class AlreadyCreateUserException() extends Exception("utilisateur déjà présent")
    
    
    def createUserTokenFromEmail(email:String) = new sun.misc.BASE64Encoder().encode(md.digest(email.getBytes))
    
    def getPlayerActorByEmail(email:String) : Future[Option[ActorRef]] = getPlayerActorByUserToken(createUserTokenFromEmail(email))
    
    def getPlayerActorByUserToken(userToken:String) : Future[Option[ActorRef]] = {
        val sel = system.actorSelection("user/"+userToken)
        val future = sel.ask(Identify(None)).mapTo[ActorIdentity].map(_.ref)
    
    /*
    system.actorSelection("user/"+userToken).resolveOne().onComplete {
      case Success(actor) => Some(actor)
      case Failure(ex) => None
    }
    */  
        future
    }
    
    def createPlayerActor(user:User) : Future[ActorRef] = {
        getPlayerActorByEmail(user.mail) map {
            case Some(_) => throw new AlreadyCreateUserException
            case None => 
                println("create player")
                val userToken = createUserTokenFromEmail(user.mail)
                system.actorOf(Props(new Player(user,userToken)), userToken)
        }

    }
}


class Player(user: User, userToken:String) extends Actor with ActorLogging {
  
  def receive = {
    case QuizActors.Login(userToken)  => 
      log.info("user {} logged in",user.firstname)

    case QuizActors.GetUser => sender ! user
  }
} 


