import akka.actor.{ActorSystem, Props}
import auction.{AuctionManager, AuctionPublisher, AuctionSearch, Notifier}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object AuctionApp extends App {
  val config = ConfigFactory.load()
  var system = ActorSystem("AuctionManager")
  val externalSystem = ActorSystem("AuctionPublisher", config.getConfig("auction-publisher").withFallback(config))

  val mainActor = system.actorOf(Props(classOf[AuctionManager]), "main")
  val auctionSearch = system.actorOf(Props(new AuctionSearch), "auctionSearch")

  mainActor ! AuctionManager.Init

  system.actorOf(Props[Notifier], "notifier")
  externalSystem.actorOf(Props[AuctionPublisher], "auctionPublisher")

  Await.result(system.whenTerminated, Duration.Inf)
}
