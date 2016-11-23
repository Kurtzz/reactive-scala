import akka.actor.{ActorSystem, Props}
import auction.{AuctionManager, AuctionSearch}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object AuctionApp extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem()
  val mainActor = system.actorOf(Props(classOf[AuctionManager]), "main")
  val auctionSearch = system.actorOf(Props(new AuctionSearch), "auctionSearch")

  mainActor ! AuctionManager.Init

  Await.result(system.whenTerminated, Duration.Inf)
}
