import akka.QuizActors
import org.reactivecouchbase.Configuration
import org.reactivecouchbase.client.ReactiveCouchbaseException
import play.api.GlobalSettings
import models._
import com.typesafe.config.{Config, ConfigFactory}

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
