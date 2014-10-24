package akka


import akka.actor.{ActorRef, ActorLogging, Actor}
import models.{Player, PlayerHistory}
import org.reactivecouchbase.client.OpResult

import scala.concurrent.Future

import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Created by djoutsop on 23/10/2014.
 */
class PlayerActor extends Actor with ActorLogging {
  import akka.QuizProtocol._

  var score: Long = 0

  var answers = IndexedSeq.empty[PlayerHistory]


  def asyncGetPlayer(mail: String, forward: ActorRef)(onSuccess: (Player, ActorRef) => Unit, onNone: (ActorRef) => Unit, onError: (Throwable, ActorRef) => Unit): Unit = {

    val result: Future[Option[Player]] = Player.findByMail(mail)

    result.map { optPlayer: Option[Player] =>
      optPlayer match {
        case Some(player) => onSuccess(player, forward)
        case None => onNone(forward)
      }
    }.recover {
      case e: Throwable => onError(e, forward)
    }
  }

  def asyncSetPlayer(player: Player, forward: ActorRef)(onSuccess: (ActorRef) => Unit, onError: (Throwable, ActorRef) => Unit): Unit = {

    val result: Future[OpResult] = Player.save(player)

    result.onSuccess {
      case status =>
        println(s"Operation status : ${status.getMessage}")
        onSuccess(forward)
    }

    result.onFailure {
      case e => onError(e, forward)
    }
  }

  def receive = {
    case CreatePlayer(user) => {

      val onSuccess = (forward: ActorRef) => {
        forward ! UserCreated(user)
        println("User saved: " + user.firstname)
      }

      val onError = (e: Throwable, forward: ActorRef) => {
        forward ! AlreadyCreated(user)
        println(s"User saved failed: ${e}")
      }

      asyncSetPlayer(Player(user), sender)(onSuccess, onError)
    }

    case Login(loginUser) => {

      val onSuccess = (player: Player, forward: ActorRef) => {

        if (player.log) {
          forward ! AlreadyLoggedIn(player.user)
        } else {

          if (loginUser.password != player.user.password) {
            println("wrong password")
            forward ! WrongPassword(loginUser)
          } else {


            val newPlayer = player.copy(log = true)

            val onSuccess = (forward: ActorRef) => {
              forward ! LoggedIn(player.user)
              println("User ${newPlayer} log in")
            }

            val onError = (e: Throwable, forward: ActorRef) => {
              println(s"User saved failed: ${e}")
              forward ! ErrorLoggedIn(loginUser)
            }

            asyncSetPlayer(newPlayer, forward)(onSuccess, onError)
          }
        }

      }

      val onNone = (forward: ActorRef) => {
        println(s"user non trouvé ${loginUser}")
        forward ! UnknownUser(loginUser)
      }

      val onError = (e: Throwable, forward: ActorRef) => {
        println(s".... ${e}")
        forward ! UnknownUser(loginUser)
      }

      asyncGetPlayer(loginUser.mail, sender)(onSuccess, onNone, onError)
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
