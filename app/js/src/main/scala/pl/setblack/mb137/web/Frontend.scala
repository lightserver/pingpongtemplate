package pl.setblack.mb137.web


import scala.scalajs.js
object Frontend extends js.JSApp {

  def main(): Unit = {
    println("frontend initialized")
    Playfield.initPlayfield()
  }
}