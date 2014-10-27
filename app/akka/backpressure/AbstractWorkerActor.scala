package akka.backpressure

import akka.actor.SupervisorStrategy.{Stop, Restart}
import akka.actor._
import akka.contrib.pattern.ClusterClient.SendToAll
import scala.concurrent.duration._


abstract class AbstractWorkerActor(clusterClient: ActorRef, registerInterval: FiniteDuration) extends Actor with ActorLogging {
  import akka.backpressure.MasterWorkerProtocol._

  override def supervisorStrategy = OneForOneStrategy() {
    case _: ActorInitializationException => Stop
    case _: DeathPactException           => Stop
    case _: Exception =>
      // TODO ADJ manage Actor Exception by returning to master the failing job
      context.become(idle)
      Restart
  }

  // Required to be implemented
  def doWork(workSender: ActorRef, work: Any): Unit

  import context.dispatcher

  // Notify the Master that we're alive
  var registerTask : Cancellable = context.system.scheduler.schedule(0.seconds, registerInterval, clusterClient, sendToMaster(WorkerCreated(self)))


  // This is the state we're in when we're working on something.
  // In this state we can deal with messages in a much more
  // reasonable manner
  def working(work: Any): Receive = {

    // Pass... we're already working
    //case WorkIsReady =>

    // Pass... we're already working
    //case NoWorkToBeDone =>

    // Pass... we shouldn't even get this
    case WorkToBeDone(_) =>
      log.error("Yikes. Master told me to do work, while I'm working.")

    // Our derivation has completed its task
    case WorkComplete(result) =>
      log.info("Work is complete.  Result {}.", result)
      sendToMaster(WorkIsDone(self))
      sendToMaster(WorkerRequestsWork(self))

      // We're idle now
      context.become(idle)

    case unknownMessage => log.error(s"??? $unknownMessage")
  }

  // In this state we have no work to do.  There really are only
  // two messages that make sense while we're in this state, and
  // we deal with them specially here
  def idle: Receive = {

    // Master says there's work to be done, let's ask for it
    case WorkIsReady =>
      log.info("Requesting work")
      sendToMaster(WorkerRequestsWork(self))

    // Send the work off to the implementation
    case WorkToBeDone(work) =>
      log.info("Got work {}", work)

      doWork(sender, work)
      context.become(working(work))

    // We asked for it, but either someone else got it first, or
    // there's literally no work to be done
    case NoWorkToBeDone => log.info("nothing to do")
    case unknownMessage => log.error(s"??? $unknownMessage")
  }

  def sendToMaster(msg: Any): Unit = {
    clusterClient ! SendToAll("/user/master/active", msg)
  }

  def receive = idle
}
