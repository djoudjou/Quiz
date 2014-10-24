package endpoint

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory

/**
 * Created by djoutsop on 24/10/2014.
 */
object BackEndPoint {
  def start(port:Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[BackendActor], name = "backend")
  }

  def startSeeds : Unit = {
    start(2551)
    start(2552)
  }
}
