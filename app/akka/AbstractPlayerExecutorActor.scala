package akka

import akka.actor._
import models.Player
import org.reactivecouchbase.client.OpResult

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global


abstract class AbstractPlayerExecutorActor extends Actor with ActorLogging {

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
}
