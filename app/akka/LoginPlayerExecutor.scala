package akka

import akka.QuizProtocol._
import akka.actor.{ActorLogging, ActorRef, Props}
import akka.backpressure.MasterWorkerProtocol.WorkComplete
import models.{User, Player}

object LoginPlayerExecutor {
  def props(): Props = Props(classOf[LoginPlayerExecutor])
}

class LoginPlayerExecutor extends AbstractPlayerExecutorActor with ActorLogging {

   def receive: Receive = {
     case Login(loginUser) =>

       val onSuccess = (player: Player, forward: ActorRef) => {

         if (player.log) {
           forward ! AlreadyLoggedIn(player.user)
         } else {

           if (loginUser.password != player.user.password) {
             println("wrong password")
             forward ! WrongPassword(loginUser)
           } else {

             val newPlayer = player.copy(log = true)

             val onSuccess = (forward: ActorRef) => {
               forward ! LoggedIn(player.user)
               println("User ${newPlayer} log in")
             }

             val onError = (e: Throwable, forward: ActorRef) => {
               println(s"User saved failed: ${e}")
               forward ! ErrorLoggedIn(loginUser)
             }

             asyncSetPlayer(newPlayer, forward)(onSuccess, onError)
           }
         }

         self ! WorkComplete("done")

       }

       val onNone = (forward: ActorRef) => {
         println(s"user non trouvé ${loginUser}")
         forward ! UnknownUser(loginUser)
         self ! WorkComplete("fail")
       }

       val onError = (e: Throwable, forward: ActorRef) => {
         println(s".... ${e}")
         forward ! UnknownUser(loginUser)
         self ! WorkComplete("fail")
       }

       //asyncGetPlayer(loginUser.mail, sender)(onSuccess, onNone, onError)

       sender ! LoggedIn(User(firstname="", lastname="", mail = loginUser.mail, password = loginUser.password))
       println(s"log >${loginUser}<")
       log.debug(s"log >${loginUser}<")
       self ! WorkComplete("done")

   }
 }
