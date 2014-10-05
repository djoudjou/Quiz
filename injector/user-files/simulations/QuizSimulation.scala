package injector

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class QuizSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:9000") // Here is the root for all relative URLs
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")
  val headers_json = Map("Content-Type" -> """application/json""")

  val usersFeed = csv("users_1.csv")

  object Player {

    val play10Question = 
        feed(usersFeed)
        .exec(http("Create_User")
          .post("/api/user")
          .headers(headers_json)  
          .body(StringBody("""{ "firstname" : "${firstname}", "lastname" : "${lastname}", "mail" : "${mail}","password" : "${mdp}"} """)).asJSON
          .check(status.is(201)))
        .pause(1)
        .exec(http("login")
          .post("/api/login")
          .headers(headers_json)  
          .body(StringBody("""{ "mail" : "${mail}", "password" : "${mdp}"} """)).asJSON
          .check(status.is(200)))
        .pause(1)
        .repeat(5, "num") {
            exec(
              session => {
                val numQuestion = session("num").as[Int]+1
                session.set("numQuestion", numQuestion)
              })
            .exec(
              http("question ${numQuestion}")
                .get("/api/question/${numQuestion}")
                .check(status.is(200))
            )
            .pause(1)
        }


  }

  val players = scenario("Players").exec(Player.play10Question)

  setUp(players.inject(atOnceUsers(100)).protocols(httpConf))
  //rampUsers(10) over (10 seconds)
  //setUp(scn.inject(rampUsers(1000) over (10 seconds)).protocols(httpConf))
}
