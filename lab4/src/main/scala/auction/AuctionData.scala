package main.scala.auction

import akka.actor.ActorRef
import org.joda.time.DateTime

sealed trait AuctionData
case object Uninitialized extends AuctionData
case class StartupData(endTime: DateTime) extends AuctionData
case class BidData(buyer: ActorRef, value: BigDecimal, buyers: List[ActorRef], notificationList: List[ActorRef]) extends AuctionData
case class Finish(buyer: ActorRef, value: BigDecimal, buyers: List[ActorRef], notificationList: List[ActorRef]) extends AuctionData {
  require(value > 0)
}
