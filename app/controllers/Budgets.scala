package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{Category, Budget}
import anorm.{Pk, NotAssigned}
import play.api.data.format.Formats._

object Budgets extends Controller {

  val budgetForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText,
      "amount" -> of[Double],
      "category_id" -> optional(of[Long])
    )(Budget.apply)(Budget.unapply)
  )

  def accountIndex(implicit token: String) = Action {
    Ok(views.html.Budgets.accountIndex(Category.allInAccount))
  }

  def newBudget(implicit token: String) = Action {
    val categoryOptions = Category.selectOptionSeq
    Ok(views.html.Budgets.newBudget(budgetForm, categoryOptions))
  }

  def createBudget(implicit token: String) = Action { implicit request =>

    budgetForm.bindFromRequest.fold(
      errors => {
        val categoryOptions = Category.selectOptionSeq
        BadRequest(views.html.Budgets.newBudget(errors, categoryOptions))
      },
      budget => {
        Budget.create(budget)
        Redirect(routes.Budgets.accountIndex(token))
      }
    )

  }

  def editBudget(implicit token: String, id: Long) = Action {
    val budget = Budget.findById(id).get
    val categoryOptions = Category.selectOptionSeq
    Ok(views.html.Budgets.editBudget(budget, budgetForm.fill(budget), categoryOptions))
  }

  def updateBudget(implicit token: String, id: Long) = Action { implicit request =>

    val budget = Budget.findById(id).get
    val categoryOptions = Category.selectOptionSeq

    budgetForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Budgets.editBudget(budget, errors, categoryOptions))
      },
      budget => {
        Budget.update(id, budget)
        Redirect(routes.Budgets.accountIndex(token))
      }
    )

  }

  def destroyBudget(implicit token: String, id: Long) = Action { implicit request =>

    Budget.destroy(id) match {
      case None => BadRequest
      case Some(budget) => Ok
    }

  }

  def totalOfBudgets(budgets: List[Budget]) = budgets.foldLeft(0.toDouble)((a, b) => a + b.amount)

}
