package auction

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import akka.event.LoggingReceive
import auction.AuctionManager.Init

import scala.util.Random

/**
  * Created by P on 27.10.2016.
  */
class AuctionManager extends Actor {
  def randInitPrice() = Random.nextInt(50) + 1

  context.actorOf(Props(new Seller(Seq("auction1", "auction2"))), "seller1")
  context.actorOf(Props(new Seller(Seq("auction3", "auction4", "auction5"))), "seller2")

  val buyers: List[ActorRef] = Range(1, 10).map(i => context.actorOf(Props(new Buyer(Random.nextInt(900) + 100)), "buyer" + i)).toList

  override def receive = LoggingReceive {
    case Init =>
        buyers.foreach(buyer => buyer ! Buyer.Start)
  }
}

object AuctionManager {
  case object Init
}
