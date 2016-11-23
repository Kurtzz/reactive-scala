package auction

/**
  * Created by P on 28.10.2016.
  */
object ActorPaths {
  val auctionSearch = "akka://AuctionManager/user/auctionSearch"
  val NotifierPath = "akka://AuctionManager/user/notifier"
  val AuctionPublisherRemotePath = "akka.tcp://AuctionPublisher@127.0.0.1:2553/user/auctionPublisher"
}
