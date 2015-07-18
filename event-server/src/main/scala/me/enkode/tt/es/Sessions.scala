package me.enkode.tt.es

import java.time.Instant
import java.util.UUID

import me.enkode.tt.models.{AssetInstanceState, AssetInstance, Id, Session}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

trait Sessions {
  implicit def executionContext: ExecutionContext
  val logger = LoggerFactory.getLogger(classOf[Sessions])

  def find(sessionId: Id, since: Option[Instant]): Future[Option[Session]] = Future {
    Some {
      def nextId() = UUID.randomUUID()

      val assetInstances = Set(
        AssetInstance(nextId(), nextId(), AssetInstanceState())
      )
      Session(assetInstances)
    }
  }
}

object Sessions {
  def apply()(implicit executionContext: ExecutionContext): Sessions = new SessionsImpl()
  class SessionsImpl()(implicit val executionContext: ExecutionContext) extends Sessions
}
