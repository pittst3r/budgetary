package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.Budget

object Budgets extends Controller {

  def index = Action {
    Ok(views.html.Budgets.index(Budget.all()))
  }

  def newBudget = Action {
    Ok(views.html.Budgets.newBudget(budgetForm))
  }

  def createBudget = Action { implicit request =>

    budgetForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Budgets.newBudget(errors))
      },
      budget => {
        Budget.create(budget.name, budget.amount)
        Redirect(routes.Budgets.index)
      }
    )

  }

  def showBudget(id: Long) = TODO

  def editBudget(id: Long) = TODO

  def updateBudget(id: Long) = TODO

  def destroyBudget(id: Long) = TODO

  val budgetForm = Form(
    mapping(
      "name" -> of[String],
      "amount" -> of[Double]
    )(Budget.apply)(Budget.unapply)
  )

}
