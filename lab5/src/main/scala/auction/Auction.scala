package main.scala.auction

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.persistence.fsm.PersistentFSM
import com.github.nscala_time.time.Imports._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.reflect._

class Auction(title: String, currentOffer: Int, baseEndTime: DateTime) extends PersistentFSM[AuctionState, AuctionData, AuctionEvent] {
  import Auction._

  override def persistenceId: String = title
  override def domainEventClassTag: ClassTag[AuctionEvent] = classTag[AuctionEvent]


  private var buyers: List[ActorRef] = List()
  private var notificationList: List[ActorRef] = List()
  private val deletionSeconds = 2

  val auctionSearch = context.actorSelection(ActorPaths.auctionSearch)
  val notifier = context.actorSelection(ActorPaths.NotifierPath)

  def startBidTimer(duration: Duration) = context.system.scheduler.scheduleOnce(
      FiniteDuration(duration seconds, TimeUnit.SECONDS), self, BidTimerExpired)

  def startDeleteTimer() = context.system.scheduler.scheduleOnce(
      FiniteDuration(deletionSeconds, TimeUnit.SECONDS), self, DeleteTimerExpired)

  startWith(NonExisted, Uninitialized)

  when(NonExisted) {
    case Event(Start(delay), _) =>
      log.info("Auction {} created.", title)
      goto(Created) applying StartEvent(baseEndTime)
  }

  when(Created) {
    case Event(Buyer.Bid(amount), _) if amount > currentOffer =>
      log.info(s"First bid: $amount from ${sender.path.name}.")
      notifier ! Notify(title, sender, amount)
      acceptOffer(amount)
      goto(Activated) applying BidEvent(sender, amount)

    case Event(Buyer.Bid(amount), bidData: BidData) if amount <= bidData.value =>
      log.info(s"Bid $amount is not enough! Current: ${bidData.value}")
      sender ! NotEnough(bidData.value)
      stay() applying BidEvent(sender, bidData.value)

    case Event(BidTimerExpired, _) =>
      log.info("Bid time expired!")
      startDeleteTimer()
      goto(Ignored) applying AuctionIgnoredEvent
  }

  when(Activated) {
    case Event(Buyer.Bid(amount), bidData: BidData) if amount > bidData.value =>
      log.info(s"Bid raised: ${bidData.value} -> $amount")
      notifier ! Notify(title, sender, amount)
      acceptOffer(amount)
      stay() applying BidEvent(sender, amount)

    case Event(Buyer.Bid(amount), bidData: BidData) if amount <= bidData.value =>
      sender ! NotEnough(bidData.value)
      log.info(s"Bid $amount is not enough! Current: ${bidData.value}")
      stay() applying BidEvent(sender, bidData.value)

    case Event(BidTimerExpired, bidData: BidData) =>
      log.info("Bid time expired!")
      context.parent ! ItemSold //Seller
      startDeleteTimer()
      log.info("Item {} sold for {} to {}.", self.path.name, bidData.value, bidData.buyer.path.name)
      goto(Sold) applying SoldEvent

    case Event(JoinNotificationList, bidData: BidData) =>
      notificationList :+= sender
      log.info(s"${sender.path.name} wants to join notification list.")
      stay() applying JoinNotificationListEvent
  }
  when(Sold) {
    case Event(DeleteTimerExpired, bidData: BidData) =>
      log.info("Auction Ended!")
      bidData.buyers.foreach(buyer => buyer ! AuctionEnded)
      stop() applying AuctionStoppedEvent
  }

  when(Ignored) {
    case Event(Restart(delay), _) =>
      log.info("Auction restarted!")
      goto(Created) applying StartEvent(baseEndTime)

    case Event(DeleteTimerExpired, bidData: BidData) =>
      bidData.buyers.foreach(buyer => buyer ! AuctionEnded)
      log.info("Auction deleted!")
      context.parent ! ItemNotSold
      stop() applying AuctionStoppedEvent
  }

  whenUnhandled {
    case Event(event, _) =>
      log.info(s"Unknown event $event")
      stay()
  }

  def acceptOffer(amount: Int) = {
    if (!buyers.contains(sender)) {
      buyers :+= sender
    }
    notificationList.foreach(buyer =>
      if (buyer != sender) buyer ! Beaten(amount)
    )
    notificationList = List()
    sender ! OfferAccepted(amount)
  }

  override def applyEvent(event: AuctionEvent, dataBeforeEvent: AuctionData): AuctionData = {
    event match {
      case StartEvent(endTime: DateTime) =>
        auctionSearch ! AuctionSearch.RegisterAuction(title)

        val now: DateTime = DateTime.now()
        if (now.isBefore(endTime)) {
          val duration: Duration = (now to endTime).toDuration
          log.info("Bid timeout: {} seconds", duration seconds)
          startBidTimer(duration)
        }
        StartupData(endTime)

      case BidEvent(buyer, value) =>
        BidData(buyer, value, buyers, notificationList)

      case SoldEvent =>
        val data = dataBeforeEvent.asInstanceOf[BidData]
        Finish(data.buyer, data.value, data.buyers, data.notificationList)

      case AuctionIgnoredEvent =>
        dataBeforeEvent

      case JoinNotificationListEvent =>
        val data = dataBeforeEvent.asInstanceOf[BidData]
        BidData(data.buyer, data.value, buyers, notificationList)

      case AuctionStoppedEvent =>
        dataBeforeEvent
    }
  }

}

object Auction {

  case class Start(delay: Long)

  case class Restart(delay: Long)

  case class OfferAccepted(offer: Int)

  case class Beaten(amount: Int)

  case class NotEnough(currentOffer: BigDecimal)

  case object ItemSold

  case object ItemNotSold

  case object AuctionNotActive

  case class AuctionEnded(auction: ActorRef)

  case object BidTimerExpired

  case object DeleteTimerExpired

  object JoinNotificationList

}