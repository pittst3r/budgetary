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
      "category_id" -> optional[Long](of[Long])
    )(Budget.apply)(Budget.unapply)
  )

  def index = Action {
    val budgets = Budget.all()
    Ok(views.html.Budgets.index(Category.all(), Budget.withoutCategory(), totalOfBudgets(budgets)))
  }

  def newBudget = Action {
    val categoryOptions = Category.selectOptionSeq()
    Ok(views.html.Budgets.newBudget(budgetForm, categoryOptions))
  }

  def createBudget = Action { implicit request =>

    budgetForm.bindFromRequest.fold(
      errors => {
        val categoryOptions = Category.selectOptionSeq()
        BadRequest(views.html.Budgets.newBudget(errors, categoryOptions))
      },
      budget => {
        Budget.create(budget)
        Redirect(routes.Budgets.index)
      }
    )

  }

  def editBudget(id: Long) = Action {
    val budget = Budget.findById(id).get
    Ok(views.html.Budgets.editBudget(budget, budgetForm.fill(budget)))
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

  def destroyBudget(id: Long) = Action { implicit request =>

    Budget.destroy(id) match {
      case None => BadRequest
      case Some(budget) => Ok
    }

  }

  def totalOfBudgets(budgets: List[Budget]) = budgets.foldLeft(0.toDouble)((a, b) => a + b.amount)

}
