package models

import play.api.Logger


case class User (
	firstname : String, 
	lastname : String, 
	mail : String,
	password : String)


object User {

  def save(user: User) {
    Logger.info("User saved: " + user.firstname)
  }
}