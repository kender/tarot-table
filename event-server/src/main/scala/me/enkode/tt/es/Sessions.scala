package me.enkode.tt.es

import java.time.Instant
import java.util.UUID

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

trait Sessions {
  implicit def executionContext: ExecutionContext
  val logger = LoggerFactory.getLogger(classOf[Sessions])

  def find(sessionId: SessionId, since: Option[Instant]): Future[Option[Session]] = Future {
    logger.trace(s"sessionId=$sessionId")
    val creator = SessionActor.human(UUID.randomUUID(), "kender")
    val sessionObjects: Set[SessionObject] = Set(
      SessionObject.Say(UUID.randomUUID(), xml.Text("hello world"), Instant.now(), creator, Instant.now, creator, Map.empty),
      SessionObject.Emote(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), creator, Instant.now, creator, Map.empty),
      SessionObject.Interactable(UUID.randomUUID(), UUID.randomUUID(), Geometry(1,2,3,math.Pi/2), Instant.now(), creator, Instant.now, creator, Map.empty)
    )
    val state = SessionState(sessionObjects)
    val session = Session(sessionId, creator, Set(SessionParticipant(creator, Participation.Creator, Instant.now)), state)

    Some {
      since.fold(session)(session.Δ)
    }
  }
}

object Sessions {
  def apply()(implicit executionContext: ExecutionContext): Sessions = new SessionsImpl()
  class SessionsImpl()(implicit val executionContext: ExecutionContext) extends Sessions
}
