package models

import play.api.Logger


case class User (
	firstname : String, 
	lastname : String, 
	mail : String,
	password : String) {

	override def toString = s"$firstname $lastname"
}

case class LoginUser (
	mail : String,
	password : String) {
	override def toString = s"$mail"
}

object User {
  def save(user: User) {
    Logger.info("User saved: " + user.firstname)
  }

  def fromString(line:String) : User = {
      val tokens = line.split(",").toList
      User(firstname = tokens(0)
      	,lastname = tokens(1)
      	,mail = tokens(2)
      	,password = tokens(3))
    }


}