import akka.{LoginPlayerExecutor, CreatePlayerExecutor, PlayerActor}
import akka.actor.{ActorSystem, AddressFromURIString, RootActorPath}
import akka.contrib.pattern.ClusterClient
import akka.japi.Util.immutableSeq
import com.typesafe.config.ConfigFactory

object StartPlayerWorker {


  def main(args: Array[String]): Unit = {
    val port = args(0).toInt
    startPlayer(port)
  }

  def startPlayer(port: Int): Unit = {
    // load worker.conf
    val conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.load("player"))

    val system = ActorSystem("PlayerSystem", conf)

    val initialContacts = immutableSeq(conf.getStringList("contact-points")).map {
      case AddressFromURIString(addr) ⇒ system.actorSelection(RootActorPath(addr) / "user" / "receptionist")
    }.toSet

    val clusterClient = system.actorOf(ClusterClient.props(initialContacts), "clusterClient")
    system.actorOf(PlayerActor.props(clusterClient,CreatePlayerExecutor.props,LoginPlayerExecutor.props), "playerWorker")
  }

}
