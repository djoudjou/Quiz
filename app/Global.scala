import akka.QuizActors
import play.api.GlobalSettings

object Global extends GlobalSettings {

  override def onStart(application: play.api.Application) {
    QuizActors
  }
  
  override def onStop(application: play.api.Application) { 
    QuizActors.system.shutdown()
  }
}
