import play.api.GlobalSettings
import akka._
import models._

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    QuizActors
    Model
  }
  
  override def onStop(application: play.api.Application) { 
    QuizActors.system.shutdown()
    Model.shutdown()
  }
}
