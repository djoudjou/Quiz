package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import models._

import scala.concurrent.Future
import akka.pattern.ask
import scala.util.{Success, Failure}

import akka.QuizActors._



import akka.QuizActors


object Users extends Controller {

  implicit val JsPathWrites = Writes[JsPath](p => JsString(p.toString))

  implicit val ValidationErrorWrites =
    Writes[ValidationError](e => JsString(e.message))

  implicit val jsonValidateErrorWrites = (
    (JsPath \ "path").write[JsPath] and
    (JsPath \ "errors").write[Seq[ValidationError]]
    tupled
  )

  // Define JSON parsers for domain model.

  implicit val userReads: Reads[User] = (
    (JsPath \ "firstname").read[String](minLength[String](2) andKeep maxLength[String](50)) and
    (JsPath \ "lastname").read[String](minLength[String](2) andKeep maxLength[String](50)) and
    (JsPath \ "mail").read[String](email andKeep minLength[String](2) andKeep maxLength[String](50)) and
    (JsPath \ "password").read[String](minLength[String](2) andKeep maxLength[String](50))
  )(User.apply _)
  
  implicit val loginReads: Reads[LoginUser] = (
    (JsPath \ "mail").read[String](email andKeep minLength[String](2) andKeep maxLength[String](50)) and
    (JsPath \ "password").read[String](minLength[String](2) andKeep maxLength[String](50))
  )(LoginUser.apply _)


  // needed to return async results
  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  
    def create = Action.async(parse.json) { implicit request =>
        val json = request.body
        
        json.validate[User].fold(
            valid = { user =>
            
                val future = QuizActors.createPlayerActor(user)
                
                
                future.map {
                    case playerActor => 
                        val response = Json.obj("status" ->"OK", "message" -> ("${playerActor.user.name} Created") )
                        Ok(response)
                }.recover {
                    case ex => BadRequest(Json.toJson(ex.getMessage))
                }
            },invalid = {
                errors => Future(BadRequest(Json.toJson(errors)))
            }
        )
    }

    def login = Action.async(parse.json) { implicit request =>
        
        val json = request.body
        
        json.validate[LoginUser].fold(
            valid = { loginUser => 
                val futureOpt = QuizActors.getPlayerActorByEmail(loginUser.mail)
                
                futureOpt.map {
                  case Some(playerActor) =>
                    val futureOpt = playerActor.ask(QuizActors.GetUser).mapTo[User]
                    futureOpt.map {
						case Some(user) =>  
						    val response = Json.obj("status" ->"OK", "message" -> ("Coucou "+ user.firstname) )
                            Ok(response).withCookies(Cookie("session_key", user.firstname))
                        case None => Unauthorized
                    }
                  case None => Unauthorized
                }.recover {
                  case ex => Unauthorized
                }
            },invalid = {
                errors => Future(BadRequest(Json.toJson(errors)))
            }
        )
    
    }
  
}