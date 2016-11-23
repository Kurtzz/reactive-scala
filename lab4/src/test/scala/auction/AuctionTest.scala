package test.scala.auction

import akka.actor.Actor.Receive
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

/**
  * Created by P on 30.10.2016.
  */
class AuctionTest extends TestKit(ActorSystem()) with WordSpecLike with BeforeAndAfterAll with ImplicitSender {

  import Auction._

  /*val auctionSearch = system.actorOf(Props[AuctionSearch], "auctionSearch")
  val auction = TestFSMRef(new Auction("auctionTest", 10), "auctionTest")
  val buyer1 = TestProbe()
  val buyer2 = TestProbe()

  "An actor" should {
    "change state to created after Start command" in {
      auction ! Start(1000)
      assert(auction.underlyingActor.stateName == Created)
    }
    "not change state after not acceptable offer" in {
      buyer1.send(auction, Buyer.Bid(1))
      buyer1.expectMsg(NotEnough(10))
      assert(auction.underlyingActor.stateName == Created)
    }
    "change state to ignored after bid time expired" in {
      auction ! BidTimerExpired
      assert(auction.underlyingActor.stateName == Ignored)
    }
    "change state to created after Restart" in {
      auction ! Restart(100)
      assert(auction.underlyingActor.stateName == Created)
    }
    "change state to active after first acceptable offer" in {
      buyer1.send(auction, Buyer.Bid(100))
      buyer1.expectMsg(OfferAccepted(100))
      buyer1.send(auction, JoinNotificationList)
      assert(auction.underlyingActor.stateName == Activated)
      assert(auction.underlyingActor.stateData == Item(100, buyer1.ref))
    }
    "not accept too low bid" in {
      buyer2.send(auction, Buyer.Bid(10))
      buyer2.expectMsg(NotEnough(100))
      assert(auction.underlyingActor.stateData == Item(100, buyer1.ref))
    }
    "accept higher bid and notify other buyers" in {
      buyer2.send(auction, Buyer.Bid(150))
      buyer2.expectMsg(OfferAccepted(150))
      buyer2.send(auction, JoinNotificationList)
      buyer1.expectMsg(Beaten(150))
      assert(auction.underlyingActor.stateData == Item(150, buyer2.ref))
    }
    "send notification to seller and change state to sold after bid time expired" in {
      auction ! BidTimerExpired
      assert(auction.underlyingActor.stateName == Sold)
    }
    "send notification AuctionEnded after DeleteTimerExpired" in {
      auction ! DeleteTimerExpired
      buyer1.expectMsg(AuctionEnded)
      buyer2.expectMsg(AuctionEnded)
    }
  }*/
}