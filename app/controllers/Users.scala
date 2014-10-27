package controllers

import akka.QuizActors._
import akka.pattern.ask
import models._
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
// All Akka messages
import akka.QuizActors
import akka.QuizProtocol._


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

        println(s"do ${json}")

        json.validate[User].fold(
            valid = { user =>
              Future(BadRequest(Json.obj("status" ->"KO", "message" -> "HELLO" )))
              QuizActors.masterProxy.ask(CreatePlayer(user)).mapTo[UserCreationMessage].map {
                    case AlreadyCreated(user) => 
                      println(s"error AlreadyCreated ${user}")
                      BadRequest(Json.obj("status" ->"KO", "message" -> s"${user.mail} deja utilisée" ))
                    case UserCreated(createdUser) => 
                      println(s"ok UserCreated ${user}")
                      Status(201)(Json.obj("status" ->"OK", "message" -> (s"[${createdUser.firstname}] Created") ))
                }.recover {
                    case ex => 
                      println(s"error ${ex}")
                      BadRequest(Json.obj("status" ->"KO", "message" -> (ex.getMessage) ))
                }
            },invalid = {
                errors => 
                  println(s"error ${errors}")
                  Future(BadRequest(Json.toJson(errors)))
            }
        )
    }


    def login = Action.async(parse.json) { implicit request =>
        
        val json = request.body
        
        json.validate[LoginUser].fold (
            valid = { loginUser => 


                QuizActors.masterProxy.ask(Login(loginUser)).mapTo[LoginMessage].map {
                    case UnknownUser(loginUser) => 
                        println(s"UnknownUser ${loginUser}")
                        Unauthorized(Json.obj("status" ->"KO", "message" -> s"${loginUser.mail} inconnu" ))
                    
                    case WrongPassword(loginUser) => 
                        println(s"WrongPassword ${loginUser}")
                        Unauthorized(Json.obj("status" ->"KO", "message" -> (s"mauvais mot de passe") ))
                    
                    case AlreadyLoggedIn(user) => 
                        println(s"error AlreadyLoggedIn ${user}")
                        BadRequest(Json.obj("status" ->"KO", "message" -> (s"[${user.firstname}] déjà loggé") ))

                    case OutOfLoginPhase =>
                        println(s"error Phase de loggin terminé")
                        BadRequest(Json.obj("status" ->"KO", "message" -> (s"phase de connexion terminé") ))                    

                    case LoggedIn(user) => 
                        println(s"LoggedIn ${user}")
                        Ok(Json.obj("status" ->"OK", "message" -> (s"[${user.firstname}] loggé") ))
                          .withCookies(Cookie("session_key", user.mail))
                }.recover {
                    case ex => 
                        println(s"error ${ex}")
                        BadRequest(Json.obj("status" ->"KO", "message" -> (ex.getMessage) ))
                }
                
            },invalid = {
                errors => 
                    println(s"error ${errors}")
                    Future(BadRequest(Json.toJson(errors)))
            }
        )
    
    }


    def question(numQuestion:Long) = Action.async{ implicit request =>
        
        val optionCookieMail : Option[Cookie] = request.cookies.get("session_key")


        optionCookieMail match {
          case None => 
              println(s"Clé de session non reconnue")
              Future(Unauthorized(Json.obj("status" ->"KO", "message" -> s"Clé de session non reconnue" )))

          case Some(cookieMail) =>
              val mail = cookieMail.value
              println(s"Clé de session ${mail}")
              val askQuestion = AskQuestion(mail,numQuestion)
              
              QuizActors.masterProxy.ask(askQuestion).mapTo[AskQuestionAnswerMessage].map {
                  case Question(question,answer_1,answer_2,answer_3,answer_4,score) =>
                      Ok(Json.obj(
                          "question" -> question, 
                          "answer_1" -> answer_1, 
                          "answer_2" -> answer_2,
                          "answer_3" -> answer_3, 
                          "answer_4" -> answer_4, 
                          "score" -> score ))

                  case WrongQuestion =>
                      BadRequest(Json.obj("status" ->"KO", "message" -> (s"mauvais numéro de question") ))

              }
        }
    
    }

}