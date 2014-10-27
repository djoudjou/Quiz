package models

import org.reactivecouchbase.ReactiveCouchbaseDriver
import org.reactivecouchbase.client.OpResult

import scala.concurrent.ExecutionContext.Implicits.global

// import the implicit JsObject reader and writer

import play.api.libs.json._

import scala.concurrent.Future


case class User(firstname: String, lastname: String, mail: String, password: String) {
  override def toString = s"$firstname $lastname"
}

case class LoginUser(mail: String, password: String) {
  override def toString = s"$mail"
}


case class Player(user:User, log:Boolean=false) {
  //var score: Long = 0
  //var answers = IndexedSeq.empty[PlayerHistory]

  override def toString = s"$user log=$log"
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

  def save(player: Player) : Future[OpResult]  = {
    Model.bucket.set[Player](player.user.mail, player)
  }

  def findByMail(mail: String): Future[Option[Player]] = {
    Model.bucket.get[Player](mail)
  }

}