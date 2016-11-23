package test.scala.auction

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import auction.Auction.{BidTimerExpired, ItemSold}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps

/**
  * Created by P on 01.11.2016.
  */
class SellerTest extends TestKit(ActorSystem()) with WordSpecLike with BeforeAndAfterAll with ImplicitSender {
  val auctionSearch = system.actorOf(Props[AuctionSearch], "auctionSearch")
  val proxy = TestProbe()
  val buyer = TestProbe()
  val seller = system.actorOf(Props(new Seller(List("sellerAuction")) {
    override def receive = {
      case x =>
        proxy.ref.forward(x)
    }
  }))
  val auction = Await.result(system.actorSelection("akka://default/user/$a/sellerAuction").resolveOne(1 second), 1 second)

  "A seller" should {
    "get notification after end of auction" in {
      buyer.send(auction, Buyer.Bid(100))
      auction ! BidTimerExpired
      proxy.expectMsg(ItemSold)
    }
  }
}
