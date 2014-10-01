package injector

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class QuizUserInjection extends Simulation {

  val nbPlayer = 10

  val httpConf = http
    .baseURL("http://localhost:9000") // Here is the root for all relative URLs
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val headers_json = Map("Content-Type" -> """application/json""") // Note the headers specific to a given request

  //val usersFeed = csv("known_users.csv")
  val usersFeed = csv("users_1.csv")
  
  val scn = scenario("Create User Scénario")
    .repeat(nbPlayer) {
      feed(usersFeed)
      .exec(http("Create_User")
        .post("/api/user")
        .headers(headers_json)  
        .body(StringBody("""{ "firstname" : "${firstname}", "lastname" : "${lastname}", "mail" : "${mail}","password" : "${mdp}"} """)).asJSON
        .check(status.is(201)))
    }

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
  //rampUsers(10) over (10 seconds)
  //setUp(scn.inject(rampUsers(1000) over (10 seconds)).protocols(httpConf))
}
