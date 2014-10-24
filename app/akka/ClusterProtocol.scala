package akka

import akka.actor.ActorRef

object ClusterProtocol {
    
    object BackendRegistration
    case class Terminated(actor:ActorRef)
    
  }
