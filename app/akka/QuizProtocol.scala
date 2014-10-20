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
    case class ErrorLoggedIn(loginUser:LoginUser) extends LoginMessage
    case class AlreadyLoggedIn(user:User) extends LoginMessage
	  case class UnknownUser(loginUser:LoginUser) extends LoginMessage
	  case class WrongPassword(loginUser:LoginUser) extends LoginMessage
    object OutOfLoginPhase extends LoginMessage

    sealed trait TimeoutMessage extends Message
    object LoginPhaseTimeout extends TimeoutMessage
    object QuestionTimeout extends TimeoutMessage
    object SynchroTimeout extends TimeoutMessage


    sealed trait QuestionMessage extends Message
    
    sealed trait AskQuestionAnswerMessage extends QuestionMessage
    
    case class AskQuestion(mail:String,numQuestion:Long) extends QuestionMessage
    case class Question(question:String,answer_1:String,answer_2:String,answer_3:String,answer_4:String,score:Long) extends AskQuestionAnswerMessage
    object WrongQuestion extends AskQuestionAnswerMessage
    
    case class Answer(mail:String, numQuestion:Long,answer:String) extends QuestionMessage
    case class AnswerStatus(are_u_right : Boolean, good_answer : String, score : Long) extends QuestionMessage
    object WrongAnswerNumber extends QuestionMessage
    object AnswerOverTimeLimit extends QuestionMessage
    object AlreadySubmittedAnswer extends QuestionMessage
    
    case class ComputeAnswer(numQuestion: Long, answer: String, expectedAnswer: String) extends QuestionMessage
    
  }
