package endpoint

import java.util.concurrent.atomic.AtomicInteger

import akka.FrontEndActor
import akka.actor._
import akka.util.Timeout
import scala.concurrent.duration.Duration
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

/**
 * Created by djoutsop on 24/10/2014.
 */
object FrontEndPoint {
  def start(port:Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    val frontend = system.actorOf(Props[FrontEndActor], name = "frontend")

    /*
    val counter = new AtomicInteger
    import system.dispatcher
    system.scheduler.schedule(2.seconds, 2.seconds) {
      implicit val timeout = Timeout(5 seconds)
      (frontend ? TransformationJob("hello-" + counter.incrementAndGet())) onSuccess {
        case result => println(result)
      }
    }
    */
  }

  def startFrontend(port: Int): ActorRef = {
    val conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).
      withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]")).
      withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", conf)
    val frontend = system.actorOf(Props[FrontEndActor], "frontend")

    //system.actorOf(Props(classOf[WorkProducer], frontend), "producer")
    //system.actorOf(Props[WorkResultConsumer], "consumer")

    frontend
  }
}
