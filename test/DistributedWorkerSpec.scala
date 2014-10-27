import akka.{CreatePlayerExecutor, PlayerActor}
import akka.QuizProtocol.{CreatePlayer, UserCreated}
import akka.actor._
import akka.backpressure.MasterActor
import akka.backpressure.MasterWorkerProtocol.WorkComplete
import akka.cluster.Cluster
import akka.contrib.pattern.{ClusterClient, ClusterSingletonManager, ClusterSingletonProxy}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import models.User
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import scala.collection.mutable.ListBuffer

import scala.concurrent.duration._

object DistributedWorkerSpec {

  val clusterConfig = ConfigFactory.parseString("""
    akka.actor.provider = "akka.cluster.ClusterActorRefProvider"
    akka.remote.netty.tcp.hostname = "127.0.0.1"
    akka.remote.netty.tcp.port=0
                                                """)

  val workerConfig = ConfigFactory.parseString("""
    akka.actor.provider = "akka.remote.RemoteActorRefProvider"
    akka.remote.netty.tcp.hostname = "127.0.0.1"
    akka.remote.netty.tcp.port=0
                                               """)
}


class LogExecutor extends Actor {

  def receive = {
    case m =>
      println(s" JOB $m")
      sender() ! WorkComplete(m)
  }
}

class DistributedWorkerSpec(_system: ActorSystem)
  extends TestKit(_system)
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll
  with ImplicitSender {

  import DistributedWorkerSpec._

  val workTimeout = 3.seconds

  def this() = this(ActorSystem("DistributedWorkerSpec", DistributedWorkerSpec.clusterConfig))

  val backendSystem: ActorSystem = {
    val config = ConfigFactory.parseString("akka.cluster.roles=[backend]").withFallback(clusterConfig)
    ActorSystem("DistributedWorkerSpec", config)
  }

  val workerSystem: ActorSystem = ActorSystem("DistributedWorkerSpec", workerConfig)

  override def beforeAll: Unit = {

  }

  override def afterAll: Unit = {
    system.shutdown()
    backendSystem.shutdown()
    workerSystem.shutdown()
    system.awaitTermination()
    backendSystem.awaitTermination()
    workerSystem.awaitTermination()
  }

  "Distributed workers" should "perform work and publish results" in {

    implicit val timeout = Timeout(600 seconds)

    val clusterAddress = Cluster(backendSystem).selfAddress

    Cluster(backendSystem).join(clusterAddress)
    backendSystem.actorOf(ClusterSingletonManager.props(MasterActor.props, "active", PoisonPill, Some("backend")), "master")

    val initialContacts = Set(workerSystem.actorSelection(RootActorPath(clusterAddress) / "user" / "receptionist"))

    val clusterClient = workerSystem.actorOf(ClusterClient.props(initialContacts), "clusterClient")

    for (n <- 1 to 3)
      workerSystem.actorOf(PlayerActor.props(clusterClient, CreatePlayerExecutor.props,Props[LogExecutor], 1.second), "worker-" + n)


    Cluster(system).join(clusterAddress)
    //val frontend = system.actorOf(Props[Frontend], "frontend")

    val results = TestProbe()
    //DistributedPubSubExtension(system).mediator ! Subscribe(MasterActor.ResultsTopic, results.ref)
    //expectMsgType[SubscribeAck]

    val masterProxy:ActorRef = system.actorOf(ClusterSingletonProxy.props(
      singletonPath = "/user/master/active",
      role = Some("backend")),
      name = "masterProxy")

    within(10.seconds) {
      awaitAssert {

        val user: User = User(firstname = "john", lastname = "doedoedoe", mail = "john.doe@gmail.com", password = "john.doe@gmail.com")
        masterProxy.tell(CreatePlayer(user),
          results.ref)
        results.expectMsg(UserCreated(user))
      }
    }

    var expectedUser = new ListBuffer[UserCreated]()

    for (n <- 2 to 10) {
      val user: User = User(firstname = "john", lastname = s"doe - $n", mail = "john.doe@gmail.com", password = "john.doe@gmail.com")
      masterProxy.tell(
        CreatePlayer(user),
        results.ref)

      //results.expectMsg(UserCreated(user))
    }

    //results.ex

    /*

    results.expectMsgType[WorkResult].workId should be("1")

    for (n <- 2 to 100) {
      frontend ! Work(n.toString, n)
      expectMsg(Frontend.Ok)
    }

    results.within(10.seconds) {
      val ids = results.receiveN(99).map { case WorkResult(workId, _) => workId }
      // nothing lost, and no duplicates
      ids.toVector.map(_.toInt).sorted should be((2 to 100).toVector)
    }
    */

  }

}