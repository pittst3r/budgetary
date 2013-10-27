package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import anorm.{Pk, NotAssigned}
import play.api.data.format.Formats._

object Allocations extends Controller {

  val allocationForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText,
      "amount" -> of[Double]
    )(AllocationWithoutAccount.apply)(AllocationWithoutAccount.unapply)
  )

  def accountIndex(implicit token: String) = Action {
    Account.findByTokenAndDo(token) { account =>
      val allocations = Allocation.allInAccount(token)
      Ok(views.html.Allocations.accountIndex(allocations))
    }
  }

  def newAllocation(implicit token: String) = TODO

  def createAllocation(implicit token: String) = TODO

  def editAllocation(implicit token: String, id: Long) = TODO

}
