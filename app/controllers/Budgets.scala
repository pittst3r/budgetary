package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.Budget
import anorm.{Pk, NotAssigned}

object Budgets extends Controller {

  val budgetForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> of[String],
      "amount" -> of[Double]
    )(Budget.apply)(Budget.unapply)
  )

  def index = Action {
    val budgets = Budget.all()
    Ok(views.html.Budgets.index(budgets, totalOfBudgets(budgets)))
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
        Budget.create(budget)
        Redirect(routes.Budgets.index)
      }
    )

  }

  def editBudget(id: Long) = Action {
    val budget = Budget.findById(id).get
    Ok(views.html.Budgets.editBudget(budget, budgetForm))
  }

  def updateBudget(id: Long) = Action { implicit request =>

    val budget = Budget.findById(id).get

    budgetForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Budgets.editBudget(budget, errors))
      },
      budget => {
        Budget.update(id, budget)
        Redirect(routes.Budgets.index)
      }
    )

  }

  def destroyBudget(id: Long) = TODO

  def totalOfBudgets(budgets: List[Budget]) = budgets.foldLeft(0.toDouble)((a, b) => a + b.amount)

}
