package main.scala.auction

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

class Buyer(maxOffer: Int) extends Actor with ActorLogging {
  import Buyer._
  import context._

  var auction: ActorRef = _
  var offer: Int = 0
  val title = "auction" + (Random.nextInt(AuctionConstants.auctionCount - 1) + 1)
  val auctionSearch = Await.result(actorSelection(ActorPaths.auctionSearch).resolveOne(1 second), 1 second)

  def scheduleSearch(title: String) = context.system.scheduler.scheduleOnce(
    FiniteDuration(Random.nextInt(5), SECONDS), auctionSearch, AuctionSearch.SearchRequest(title))

  def scheduleBid(auction: ActorRef, amount: Int) = {
//    log.info(s"Schedule bid by ${self.path.name}, auction: ${auction.path.name}, amount: $amount")
    context.system.scheduler.scheduleOnce(
      FiniteDuration(Random.nextInt(10) + 5, SECONDS), auction, Bid(amount))
  }

  def receive: Receive = {
    case Start =>
      scheduleSearch(title)
      log.info(s"Buyer is searching for an acution: $title")

    case AuctionSearch.SearchResponse(results: Seq[ActorRef]) =>
      if (results.nonEmpty) {
        auction = results.head
        log.info(s"Found auction: ${auction.path.name}!")
        scheduleBid(auction, nextRandomBid(offer))
      } else {
        log.info("Auction not found!")
        scheduleSearch(title)
      }

    case Auction.OfferAccepted(amount) =>
      log.info(s"Offer accepted: $amount")
      auction ! Auction.JoinNotificationList

    case Auction.Beaten(amount) =>
      log.info(s"Higher bid: $amount")
      if (amount < maxOffer) {
        auction ! Bid(nextRandomBid(amount))
      } else {
        log.info(s"It's too expensive for me: $amount > $maxOffer")
      }

    case Auction.NotEnough(amount) =>
      log.info(s"Offer was too low, current offer: $amount")
      if (amount < maxOffer) {
        sender ! Bid(nextRandomBid(amount.toInt))
      } else {
        log.info(s"It's too expensive for me: $amount > $maxOffer")
      }

    case Auction.AuctionEnded =>
      stop(self)

    case Auction.ItemSold =>
//      currentOffers -= sender

    case Auction.AuctionNotActive =>
      scheduleBid(sender, Random.nextInt(100) + 1)
  }

  def nextRandomBid(amount: Int): Int = {
    amount + Random.nextInt(maxOffer - amount - 1) + 1
  }
}

object Buyer {
  case object Start
  case class Bid(amount: Int) {
    require(amount > 0)
  }
}

