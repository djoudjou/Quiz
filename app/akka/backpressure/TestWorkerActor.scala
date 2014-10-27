package akka.backpressure

import akka.actor.{ActorRef, Props}


import scala.concurrent.Future
import scala.concurrent.duration._

object TestWorkerActor {
  def props(clusterClient: ActorRef,registerInterval: FiniteDuration = 10 seconds): Props = Props(classOf[TestWorkerActor], clusterClient, registerInterval)
}

class TestWorkerActor(clusterClient: ActorRef,registerInterval: FiniteDuration) extends AbstractWorkerActor(clusterClient, registerInterval) {
  import akka.backpressure.MasterWorkerProtocol._
  // We'll use the current dispatcher for the execution context.
  // You can use whatever you want.
  implicit val ec = context.dispatcher

  def doWork(workSender: ActorRef, msg: Any): Unit = {
    Future {
      workSender ! msg
      self ! WorkComplete("done")
    }
  }
}
