package main.scala.auction

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.joda.time.DateTime

import scala.util.Random

/**
  * Created by P on 28.10.2016.
  */
class Seller(auctionTitles: Seq[String]) extends Actor with ActorLogging {
  var auction: ActorRef = _
  private val MaxAuctionDurationSeconds = 60 * 5
  private val endTime: DateTime = DateTime.now().plusSeconds(Random.nextInt(MaxAuctionDurationSeconds) + 1)

  auctionTitles.foreach(title => {
    auction = context.actorOf(Props(classOf[Auction], title, Random.nextInt(10) + 1, endTime), title)
    auction ! Auction.Start(1)
  })

  override def receive: Receive = {
    case Auction.ItemSold =>
      log.info("You sold: {}", sender.path.name)
    case Auction.ItemNotSold =>
      log.info("You didn't sell {}", sender.path.name)
  }
}
