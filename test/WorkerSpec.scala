import akka.actor.{ActorRef, ActorSystem, Props}
import akka.backpressure.{MasterActor, TestWorkerActor}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}


class WorkerSpec extends TestKit(ActorSystem("WorkerSpec"))
with Matchers
with FlatSpecLike
with BeforeAndAfterAll
with ImplicitSender {


  override def afterAll(): Unit ={
    system.shutdown()
  }

  def worker(name: String, master:ActorRef) = {
    system.actorOf(TestWorkerActor.props(master))
  }

  "Worker" should "work" in {
      // Spin up the master
      val m = system.actorOf(Props[MasterActor], "master")
      // Create three workers
      val w1 = worker("master",m)
      val w2 = worker("master",m)
      val w3 = worker("master",m)
      // Send some work to the master
      m ! "Hithere"
      m ! "Guys"
      m ! "So"
      m ! "What's"
      m ! "Up?"
      // We should get it all back
      expectMsgAllOf("Hithere", "Guys", "So", "What's", "Up?")
  }

}
