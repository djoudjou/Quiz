package akka.backpressure

import akka.actor.ActorRef

object MasterWorkerProtocol {
  // Messages from Workers
  case class WorkIsDone(worker: ActorRef)
  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)

  // Messages to Workers
  case class WorkToBeDone(work: Any)
  case object WorkIsReady
  case object NoWorkToBeDone

  // This is how our derivations will interact with us.  It
  // allows derivations to complete work asynchronously
  case class WorkComplete(result: Any)
}
