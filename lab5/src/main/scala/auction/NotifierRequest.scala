package main.scala.auction

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import akka.pattern.ask

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by P on 2016-11-23.
  */
class NotifierRequest(notification: Notify) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  override def preStart(): Unit = {
    self ! notification
  }

  override def receive: Receive = {
    case notification: Notify =>
      val future = context.actorSelection(ActorPaths.AuctionPublisherRemotePath) ? notification
      Await.result(future, timeout.duration)
  }
}
