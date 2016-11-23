package auction

import akka.actor.ActorRef

/**
  * Created by P on 2016-11-23.
  */
sealed trait PublisherMessage
case class Notify(auctionTitle: String, buyer: ActorRef, value: BigDecimal) extends PublisherMessage
