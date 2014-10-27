import akka.actor.{ActorSystem, PoisonPill}
import akka.backpressure.MasterActor
import akka.contrib.pattern.ClusterSingletonManager
import com.typesafe.config.ConfigFactory

object StartBackend {


  def main(args: Array[String]): Unit = {
    val port = args(0).toInt
    startBackend(port)
  }

  def startBackend(port: Int): Unit = {
    val role = "backend"
    val conf = ConfigFactory.parseString(s"akka.cluster.roles=[$role]").
      withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", conf)

    system.actorOf(ClusterSingletonManager.props(MasterActor.props, "active", PoisonPill, Some(role)), "master")
  }

}
