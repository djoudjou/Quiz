package controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import models.User

import scala.concurrent.Future
import akka.pattern.ask
import scala.util.{Success, Failure}

import akka.QuizActors._



import akka.QuizActors


object Users extends Controller {

  // Define serialisation for JSON validation error messages.
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

  implicit val loginReads = (
    (__ \ "mail").read[String] and
    (__ \ "password").read[String]
  ) tupled

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
        request.body.validate[(String, String)].map{ 
            case (mail, password) => {
                
                val future = QuizActors.getPlayerActorByEmail(mail).flatMap{
                  playerActorOpt =>
                    playerActorOpt.flatMap {
                      playerActor => playerActor ? QuizActors.GetUser
                    }
                }
                
                future.map {
                    case user => 
                        if(user.password != password) {
                            Unauthorized
                        } else {
                            val response = Json.obj("status" ->"OK", "message" -> ("Coucou "+ user.firstname) )
                            Ok(response).withCookies(Cookie("session_key", playerActor.userToken))
                        }
                    
                }.recover{
                    case ex => Unauthorized
                }
            }
        }.recoverTotal{
            e => Future(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(e))))
        }
    
    }
  
}