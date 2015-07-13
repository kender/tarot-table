package me.enkode.tt.es

import java.util.UUID

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

trait Sessions {
  implicit def executionContext: ExecutionContext
  val logger = LoggerFactory.getLogger(classOf[Sessions])

  def find(sessionId: SessionId): Future[Option[Session]] = Future {
    logger.trace(s"sessionId=$sessionId")
    val creator = SessionActor.human(UUID.randomUUID(), "kender")
    val session = Session(sessionId, creator, Set(SessionParticipant(creator, Participation.Creator)), SessionState(Set.empty))
    Some(session)
  }
}

object Sessions {
  def apply()(implicit executionContext: ExecutionContext): Sessions = new SessionsImpl()
  class SessionsImpl()(implicit val executionContext: ExecutionContext) extends Sessions
}
