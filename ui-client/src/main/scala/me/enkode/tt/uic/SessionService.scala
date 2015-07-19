package me.enkode.tt.uic

import java.util.UUID

import me.enkode.tt.models._
import org.scalajs.dom

import scala.concurrent.Future
import upickle.default._

object SessionService extends ServiceHelpers {
  import dom.ext.Ajax
  import scalajs.concurrent.JSExecutionContext.Implicits.queue

  def getSession(sessionId: UUID): Future[Session] = {
    Ajax.get(s"/session/$sessionId").flatMap(xhrResult[Session])
  }

  def clearSession(sessionId: UUID): Future[Session] = {
    Ajax.post(s"/session/$sessionId/clear").flatMap(xhrResult[Session])
  }

  def instantiateAsset(sessionId: UUID, assetInstance: AssetInstance): Future[Session] = {
    Ajax.post(
      s"/session/$sessionId/asset",
      write(assetInstance),
      headers = Map("content-type" → "application/json")
    ).flatMap(xhrResult[Session])
  }
}
