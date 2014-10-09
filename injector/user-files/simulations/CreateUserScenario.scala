package injector

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

import Headers._

object CreateUserScenario {
	
	def buildScenario() = {
		val usersFeed = csv(Parameters.userFileName)
		
		feed(usersFeed)
        .exec(http("Create_User")
          .post("/api/user")
          .headers(headers_1)  
          .body(StringBody("""{ "firstname" : "${firstname}", "lastname" : "${lastname}", "mail" : "${mail}","password" : "${mdp}"} """)).asJSON
          .check(status.is(201)))
        .pause(1)
	}
}
