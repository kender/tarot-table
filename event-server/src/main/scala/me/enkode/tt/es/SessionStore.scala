package me.enkode.tt.es

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, Actor}
import akka.util.Timeout
import me.enkode.tt.models._

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}

trait SessionStore {
  import akka.pattern.ask
  import SessionStoreActor._

  def sessionStoreActorRef: ActorRef

  implicit val backendTimeout: Timeout
  implicit val executionContext: ExecutionContext

  def findSession(sessionId: Id): Future[Option[Session]] = {
    (sessionStoreActorRef ? FindSession(sessionId)).mapTo[FindSessionReply].map(_.session)
  }

  def saveSession(sessionId: Id, session: Session): Future[Session] = {
    (sessionStoreActorRef ? SaveSession(sessionId, session)).mapTo[SaveSessionReply].map(_.session)
  }
}

object SessionStoreActor {
  def props(): Props = Props(new SessionStoreActor())

  case class FindSession(sessionId: Id)
  case class FindSessionReply(session: Option[Session])

  case class SaveSession(sessionId: Id, session: Session)
  case class SaveSessionReply(sessionId: Id, session: Session)
}
class SessionStoreActor() extends Actor {
  import SessionStoreActor._

  val sessions = TrieMap.empty[Id, Session]

  override def receive = {
    case FindSession(sessionId) ⇒
      sender() ! FindSessionReply(sessions.get(sessionId))

    case SaveSession(sessionId, session) ⇒
      sessions.update(sessionId, session)
      sender() ! SaveSessionReply(sessionId, session)
  }
}

object SessionStore {
  class SessionStoreImpl(
    val sessionStoreActorRef: ActorRef,
    val backendTimeout: Timeout)
    (implicit val executionContext: ExecutionContext)
    extends SessionStore

  def apply(sessionStoreActorRef: ActorRef, backendTimeout: Timeout = Timeout(60, TimeUnit.SECONDS))
    (implicit executionContext: ExecutionContext): SessionStore = {
    new SessionStoreImpl(sessionStoreActorRef, backendTimeout)
  }
}
