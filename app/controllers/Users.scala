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
// All Akka messages
import akka.QuizProtocol._


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
                QuizActors.playerSupervisor.ask(CreatePlayer(user)).mapTo[UserCreationMessage].map {
                    case AlreadyCreated(user) => 
                      println(s"error AlreadyCreated ${user}")
                      BadRequest(Json.obj("status" ->"KO", "message" -> s"${user.mail} deja utilisée" ))
                    case UserCreated(createdUser) => 
                      println(s"ok UserCreated ${user}")
                      Status(201)(Json.obj("status" ->"OK", "message" -> (s"[${createdUser.firstname}] Created") ))
                }.recover {
                    case ex => 
                      println("error")
                      BadRequest(Json.obj("status" ->"KO", "message" -> (ex.getMessage) ))
                }
            },invalid = {
                errors => 
                  println("error")
                  Future(BadRequest(Json.toJson(errors)))
            }
        )
    }


    def login = Action.async(parse.json) { implicit request =>
        
        val json = request.body
        
        json.validate[LoginUser].fold (
            valid = { loginUser => 


                QuizActors.playerSupervisor.ask(Login(loginUser)).mapTo[LoginMessage].map {
                    case UnknownUser(loginUser) => Unauthorized(Json.obj("status" ->"KO", "message" -> s"${loginUser.mail} inconnu" ))
                    
                    case WrongPassword(loginUser) => Unauthorized(Json.obj("status" ->"KO", "message" -> (s"mauvais mot de passe") ))
                    
                    case AlreadyLoggedIn(user) => BadRequest(Json.obj("status" ->"KO", "message" -> (s"[${user.firstname}] déjà loggé") ))

                    case LoggedIn(user) => Ok(Json.obj("status" ->"OK", "message" -> (s"[${user.firstname}] loggé") ))
                                            .withCookies(Cookie("session_key", user.mail))
                }.recover {
                    case ex => BadRequest(Json.obj("status" ->"KO", "message" -> (ex.getMessage) ))
                }
                
            },invalid = {
                errors => Future(BadRequest(Json.toJson(errors)))
            }
        )
    
    }
  
}