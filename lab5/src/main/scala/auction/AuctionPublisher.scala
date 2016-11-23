package main.scala.auction

import akka.actor.{Actor, ActorLogging, Status}

/**
  * Created by P on 2016-11-23.
  */
class AuctionPublisher extends Actor with ActorLogging {

  private val MaxCounter: Int = 3

  private var counter = 0

  override def receive: Receive = {
    case notify: Notify =>
      if (counter < MaxCounter) {
        log.info(s"Received notification: [${notify.auctionTitle}, ${notify.buyer.path.name}, ${notify.value}].")
        counter += 1
        sender() ! Status.Success
      } else {
        log.error(s"Error for: [${notify.auctionTitle}, ${notify.buyer.path.name}, ${notify.value}].")
        counter = 0
        sender() ! Status.Failure(new IllegalStateException())
      }
  }
}
