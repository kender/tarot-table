package me.enkode.tt.es

import java.time.Instant

import me.enkode.tt.http.SerializationSupport._
import spray.json.DefaultJsonProtocol._
import spray.json._


trait Named {
  lazy val name = getClass.getSimpleName.replaceAllLiterally("$", "").toLowerCase
}

trait Lookup[T <: Named] {
  def all: Set[T]
  def findByName(name: String): Option[T] = all.find(_.name == name.toLowerCase)
}

sealed trait SessionActorType extends Named
object SessionActorType extends Lookup[SessionActorType] {
  case object Human extends SessionActorType
  case object System extends  SessionActorType
  case object Clock extends SessionActorType

  override val all: Set[SessionActorType] = Set(Human, System, Clock)

  implicit object JsonFormat extends JsonFormat[SessionActorType] {
    override def read(json: JsValue) = SessionActorType
      .findByName(json.convertTo[String])
      .getOrElse(sys.error(s"invalid SessionActorType: $json"))
    override def write(obj: SessionActorType) = JsString(obj.name)
  }
}

case class SessionActor(sessionActorType: SessionActorType, id: SessionActorId, displayName: String)
object SessionActor {
  implicit val jsonFormat = jsonFormat3(SessionActor.apply)
  def human(id: SessionActorId, displayName: String) = SessionActor(SessionActorType.Human, id, displayName)
}

case class Geometry(x: Double, y: Double, z: Double, θ: Double)
object Geometry {
  implicit val jsonFormat = jsonFormat4(Geometry.apply)
}

sealed trait SessionObject extends Named {
  def id: SessionObjectId
  def createdAt: Instant
  def createdBy: SessionActorType
  def modifiedAt: Instant
  def modifiedBy: SessionActorType
  def meta: Meta
}

object SessionObject {
  case class Interactable(id: SessionObjectId, assetId: AssetId, geometry: Geometry, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType, meta: Meta) extends SessionObject
  object Interactable {
    implicit val jsonFormat = jsonFormat8(Interactable.apply)
  }
  case class Say(id: SessionObjectId, sessionActor: SessionActor, text: RichText, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType, meta: Meta) extends SessionObject
  object Say {
    implicit val jsonFormat = jsonFormat8(Say.apply)
  }
  case class Emote(id: SessionObjectId, sessionActor: SessionActor, assetId: AssetId, createdAt: Instant, createdBy: SessionActorType, modifiedAt: Instant, modifiedBy: SessionActorType, meta: Meta) extends SessionObject
  object Emote {
    implicit val jsonFormat = jsonFormat8(Emote.apply)
  }

  implicit object JsonFormat extends JsonFormat[SessionObject] {
    override def read(json: JsValue) = {
      val jsObject = json.asJsObject
      val JsString(oType) :: Nil = jsObject.getFields("type")
      oType match {
        case "Interactable" ⇒ json.convertTo[Interactable]
        case "Say" ⇒ json.convertTo[Say]
        case "Emote" ⇒ json.convertTo[Emote]
      }
    }

    override def write(obj: SessionObject) = obj match {
      case interactable: Interactable ⇒
        JsObject(interactable.toJson.asJsObject.fields + ("type" → JsString("Interactable")))
      case say: Say ⇒
        JsObject(say.toJson.asJsObject.fields + ("type" → JsString("Say")))
      case emote: Emote ⇒
        JsObject(emote.toJson.asJsObject.fields + ("type" → JsString("Emote")))
    }
  }
}


case class SessionState(objects: Set[SessionObject]) {
  def Δ(since: Instant): SessionState = {
    copy(this.objects.filter(_.modifiedAt.isAfter(since)))
  }
}
object SessionState {
  implicit val jsonFormat = jsonFormat1(SessionState.apply)
}

sealed trait Participation extends Named
object Participation extends Lookup[Participation] {
  case object Creator extends Participation
  case object Full extends Participation
  case object Chat extends Participation
  case object View extends Participation
  override val all: Set[Participation] = Set(Creator, Full, Chat, View)

  implicit object JsonFormat extends JsonFormat[Participation] {
    override def read(json: JsValue) = findByName(json.convertTo[String]).getOrElse(sys.error(s"invalid participation: $json"))
    override def write(obj: Participation) = JsString(obj.name)
  }
}

case class SessionParticipant(actor: SessionActor, participation: Participation)
object SessionParticipant {
  implicit val jsonFormat = jsonFormat2(SessionParticipant.apply)
}

case class Session(id: SessionId, creator: SessionActor, participants: Set[SessionParticipant], state: SessionState)
object Session {
  implicit val jsonFormat = jsonFormat4(Session.apply)
}
