package akka

import akka.QuizProtocol.{AlreadyCreated, CreatePlayer, UserCreated}
import akka.actor.{ActorLogging, ActorRef, Props}
import akka.backpressure.MasterWorkerProtocol.WorkComplete


object CreatePlayerExecutor {
  def props(): Props = Props(classOf[CreatePlayerExecutor])
}

class CreatePlayerExecutor extends AbstractPlayerExecutorActor with ActorLogging {

  def receive: Receive = {
    case CreatePlayer(user) => {

      val onSuccess = (forward: ActorRef) => {
        forward ! UserCreated(user)
        println("User saved: " + user.firstname)
        self ! WorkComplete("done")
      }

      val onError = (e: Throwable, forward: ActorRef) => {
        forward ! AlreadyCreated(user)
        println(s"User saved failed: ${e}")
        self ! WorkComplete("fail")
      }

      //asyncSetPlayer(Player(user), sender)(onSuccess, onError)
      Thread.sleep(1000)
      sender ! UserCreated(user)
      log.info(s"create >${user}<")
      context.parent ! WorkComplete("done")
    }
    case unknownMessage => log.error(s"??? $unknownMessage")
  }
}
