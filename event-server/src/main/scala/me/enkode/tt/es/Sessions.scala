package me.enkode.tt.es

trait Sessions {
  def find(sessionId: SessionId): Option[Session] = None
}

object Sessions {
  class SessionsImpl() extends Sessions

  def apply(): Sessions = new SessionsImpl()
}
