package akka
import models._

object QuizProtocol {
    
    sealed trait Message


    sealed trait GameMessage extends Message
    case class CreateGame(game:Game) extends GameMessage
    
    sealed trait UserCreationMessage extends Message
    
    case class CreatePlayer(user:User) extends Message
    case class AlreadyCreated(user:User) extends UserCreationMessage
    case class UserCreated(user:User) extends UserCreationMessage
    
    sealed trait LoginMessage extends Message

    case class Login(loginUser:LoginUser) extends Message
    case class LoggedIn(user:User) extends LoginMessage
    case class AlreadyLoggedIn(user:User) extends LoginMessage
	case class UnknownUser(loginUser:LoginUser) extends LoginMessage
	case class WrongPassword(loginUser:LoginUser) extends LoginMessage

    object LoginPhaseTimeout extends Message


    case class AskQuestion(numQuestion:Long) extends Message
    case class Question(question:String,answer_1:String,answer_2:String,answer_3:String,answer_4:String) extends Message
    case class Answer(numQuestion:Long,answer:String) extends Message
  }
