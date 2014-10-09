package injector

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


import Headers._

object GameScenario {
	
	def buildScenario() = {
		val usersFeed = csv(Parameters.userFileName)
		
		feed(usersFeed)
		.exec(http("login")
          .post("/api/login")
          .headers(headers_1)  
          .body(StringBody("""{ "mail" : "${mail}", "password" : "${mdp}"} """)).asJSON
          .check(status.is(200)))
        .repeat(Parameters.nbQuestion, "num") {
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
}
