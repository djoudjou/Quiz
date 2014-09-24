package akka

import akka.actor._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import scala.language.postfixOps
import scala.concurrent.duration._

import controllers.ChatApplication
import org.joda.time.DateTime
import scala.util.Random

object QuizActors {
  
  /** Quiz actor system */
  val system = ActorSystem("quiz")

  /** Supervisor for Romeo and Juliet */
  val supervisor = system.actorOf(Props(new Supervisor()), "PlayerSupervisor")

  case object Login(userToken:String)
  case object AskQuestion(numQuestion:int)
  case object Question(question:String,answer_1:String,answer_2:String,answer_3:String,answer_4:String)
  case object Answer(numQuestion:int,answer:String)
  
  def getPlayerActor(userToken:String) = {
      context.actorOf(Props(new Chatter("Juliet", Quotes.juliet)))
  }
}

/** Supervisor initiating Romeo and Juliet actors and scheduling their talking */
class Supervisor() extends Actor {

  val juliet = context.actorOf(Props(new Chatter("Juliet", Quotes.juliet)))
  context.system.scheduler.schedule(1 seconds, 8 seconds, juliet, ChatActors.Talk)
  
  val romeo = context.actorOf(Props(new Chatter("Romeo", Quotes.romeo)))
  context.system.scheduler.schedule(5 seconds, 8 seconds, romeo, ChatActors.Talk)

  def receive = { case _ => }
}

/** Chat participant actors picking quotes at random when told to talk */
class Player(name: String, quotes: Seq[String]) extends Actor {
  
  def receive = {
    case ChatActors.Talk  => {
      val now: String = DateTime.now.toString
      val quote = quotes(Random.nextInt(quotes.size))
      val msg = Json.obj("room" -> "room1", "text" -> quote, "user" ->  name, "time" -> now )

      ChatApplication.chatChannel.push(msg)
    }
  }
} 


