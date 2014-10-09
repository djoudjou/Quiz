package injector

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import CreateUserScenario._

class CreateUserSimulation extends Simulation {

  val httpConf = http.baseURL(Parameters.urlBase).userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
  
  val createPlayers = scenario("createPlayers").exec(CreateUserScenario.buildScenario())

  setUp(createPlayers.inject(atOnceUsers(Parameters.nbPlayer)).protocols(httpConf))
  //rampUsers(10) over (10 seconds)
  //setUp(scn.inject(rampUsers(1000) over (10 seconds)).protocols(httpConf))
}
