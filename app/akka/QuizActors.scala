package akka

import akka.actor._
import akka.contrib.pattern.ClusterSingletonProxy
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.language.postfixOps



object QuizActors {

  implicit val timeout = Timeout(600 seconds)

  //val frontendActor = FrontEndPoint.startFrontend(0)

  /** Quiz actor system */
  val system = ActorSystem("ClusterSystem", ConfigFactory.load())

  lazy val masterProxy = system.actorOf(ClusterSingletonProxy.props(
                            singletonPath = "/user/master/active",
                            role = Some("backend")),
                            name = "masterProxy")
}