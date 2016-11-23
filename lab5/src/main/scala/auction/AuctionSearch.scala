package main.scala.auction

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.collection.mutable

/**
  * Created by P on 28.10.2016.
  */
class AuctionSearch extends Actor with ActorLogging {
  import AuctionSearch._

  var auctions: mutable.HashMap[String, ActorRef] = mutable.HashMap()

  override def receive: Receive = {
    case RegisterAuction(title: String) =>
      log.info(s"Registering title: $title")
      auctions += (title -> sender)
    case SearchRequest(string: String) =>
      log.info(s"Searching for: $string")
      sender ! SearchResponse(auctions.filter(_._1.contains(string)).values.toList)
  }
}

object AuctionSearch {
  case class RegisterAuction(title: String)
  case class SearchRequest(titlePart: String)
  case class SearchResponse(results: Seq[ActorRef])
}
