package pl.setblack.lsa.basic

case class PlayfieldState( pingMessages : Seq[PingMessage], pongMessages : Seq[PingMessage]) {
  def ping(msg : PingMessage) = {
    copy( pingMessages :+ msg)
  }
  def pong(msg : PingMessage) = {
    copy( pongMessages :+ msg)
  }
}

case class PingMessage( msg: String, author : String)


