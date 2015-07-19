package me.enkode.tt.es

import java.time.Instant
import java.util.UUID

import me.enkode.tt.models.{AssetInstance, Id, Session}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

trait Sessions {
  implicit def executionContext: ExecutionContext
  val sessionStore: SessionStore

  val logger = LoggerFactory.getLogger(classOf[Sessions])

  /* some test data */
  val testSessions = Map(
    "A74E5A49-4B12-4F7B-82E7-3A950FDCE107" → Session(Set.empty)
  ) map { case (sessionId, session) ⇒
    sessionStore.saveSession(UUID.fromString(sessionId), session)
  }

  def find(sessionId: Id, since: Option[Instant] = None): Future[Option[Session]] = {
    sessionStore.findSession(sessionId)
  }

  def addAssetInstance(sessionId: Id, assetInstance: AssetInstance): Future[Option[Session]] = {
    def updateSession(session: Option[Session]): Option[Session] = {
      session map { s ⇒ s.copy(s.assetInstances + assetInstance) }
    }

    def saveSession(session: Option[Session]): Future[Option[Session]] = {
      session.fold(Future.successful(Option.empty[Session])) { session ⇒
        sessionStore.saveSession(sessionId, session).map(s ⇒ Some(s))
      }
    }

    find(sessionId).map(updateSession).flatMap(saveSession)
  }

  def clear(sessionId: Id): Future[Option[Session]] = {
    def updateSession(session: Option[Session]): Option[Session] = {
      session map { s ⇒ s.copy(Set.empty) }
    }

    def saveSession(session: Option[Session]): Future[Option[Session]] = {
      session.fold(Future.successful(Option.empty[Session])) { session ⇒
        sessionStore.saveSession(sessionId, session).map(s ⇒ Some(s))
      }
    }

    find(sessionId).map(updateSession).flatMap(saveSession)
  }
}

object Sessions {
  class SessionsImpl(val sessionStore: SessionStore)
    (implicit val executionContext: ExecutionContext) extends Sessions
  def apply(sessionStore: SessionStore)(implicit executionContext: ExecutionContext): Sessions = {
    new SessionsImpl(sessionStore)
  }
}
