package models

import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import org.reactivecouchbase.ReactiveCouchbaseDriver

// import the implicit JsObject reader and writer

import org.reactivecouchbase.CouchbaseRWImplicits.documentAsJsObjectReader
import org.reactivecouchbase.CouchbaseRWImplicits.jsObjectToDocumentWriter
import scala.concurrent.Future
import play.api.libs.json._


case class User(firstname: String, lastname: String, mail: String, password: String) {
  override def toString = s"$firstname $lastname"
}

case class Player(user:User, log:Boolean=false) {
  override def toString = s"$user log=$log"
}


case class LoginUser(mail: String, password: String) {
  override def toString = s"$mail"
}


object Model {
  // get a driver instance driver
  val driver = ReactiveCouchbaseDriver()
  // get the default bucket
  val bucket = driver.bucket("quiz")

  def shutdown(): Unit = {
    // shutdown the driver (only at app shutdown)
    driver.shutdown()
  }
}

// classic usage of bucket for an entity
object Player {

  implicit val userFmt = Json.format[User]
  implicit val playerFmt = Json.format[Player]

  def save(player: Player)  = {
    Model.bucket.set[Player](player.user.mail, player)
  }

  def findByMail(mail: String): Future[Option[Player]] = {
    /*
    bucket.get[User](mail).map { opt =>
      println(opt.map(person => s"Found John : ${person}").getOrElse("Cannot find object with key 'john-doe'"))
    }
    */
    Model.bucket.get[Player](mail)
  }

  //
  //  def fromString(line:String) : User = {
  //    val tokens = line.split(",").toList
  //    User(firstname = tokens(0)
  //      ,lastname = tokens(1)
  //      ,mail = tokens(2)
  //      ,password = tokens(3))
  //  }


}