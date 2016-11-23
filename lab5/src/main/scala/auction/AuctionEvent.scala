package main.scala.auction

import akka.actor.ActorRef
import org.joda.time.DateTime

/**
  * Created by P on 14.11.2016.
  */
sealed trait AuctionEvent
case class StartEvent(endTime: DateTime) extends AuctionEvent
case class BidEvent(buyer: ActorRef, value: BigDecimal) extends AuctionEvent
case object SoldEvent extends AuctionEvent
case object AuctionIgnoredEvent extends AuctionEvent
case object JoinNotificationListEvent extends AuctionEvent
case object AuctionStoppedEvent extends AuctionEvent
