package test.scala.auction

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import auction.AuctionSearch.{RegisterAuction, SearchRequest, SearchResponse}
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import scala.concurrent.duration._


import scala.collection.mutable

/**
  * Created by P on 02.11.2016.
  */
class AuctionSearchTest extends TestKit(ActorSystem()) with WordSpecLike with BeforeAndAfterAll with ImplicitSender {
  val auctionSearch = TestActorRef[AuctionSearch]
  val test1 = TestProbe()
  val test2 = TestProbe()
  val test3 = TestProbe()
  val test4 = TestProbe()
  val test5 = TestProbe()
  val test6 = TestProbe()

  "An auctionSearch" should {
    "register an auction and add it to Map" in {
      test1.send(auctionSearch, RegisterAuction("auctionFoo1"))
      assert(auctionSearch.underlyingActor.auctions.contains("auctionFoo1"))
      assert(auctionSearch.underlyingActor.auctions("auctionFoo1").equals(test1.ref))
    }
    "return correct response" in {
      val map = mutable.HashMap(
        "auctionFoo1" -> test1.ref,
        "auctionAGH1" -> test2.ref,
        "auctionFoo2" -> test3.ref,
        "auctionAGH2" -> test4.ref,
        "auctionFoo3" -> test5.ref,
        "auctionAGH3" -> test6.ref
      )
      auctionSearch.underlyingActor.auctions = map

      test1.send(auctionSearch, SearchRequest("Foo"))
      val result = test1.receiveOne(1 second).asInstanceOf[SearchResponse].results
      assert(result.contains(test1.ref))
      assert(result.contains(test3.ref))
      assert(result.contains(test5.ref))
      assert(!result.contains(test2.ref))
      assert(!result.contains(test4.ref))
      assert(!result.contains(test6.ref))
    }
  }
}
