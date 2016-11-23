package auction

import akka.persistence.fsm.PersistentFSM.FSMState

/**
  * Created by P on 20.10.2016.
  */
sealed trait AuctionState extends FSMState
case object NonExisted extends AuctionState {
  override def identifier: String = "non_existed"
}
case object Created extends AuctionState {
  override def identifier: String = "created"
}
case object Activated extends AuctionState{
  override def identifier: String = "activated"
}
case object Ignored extends AuctionState{
  override def identifier: String = "ignored"
}
case object Sold extends AuctionState{
  override def identifier: String = "sold"
}
