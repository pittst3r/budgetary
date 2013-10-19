package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{Category, Account}
import anorm.{Pk, NotAssigned}
import play.api.data.format.Formats._

object Categories extends Controller {

  val categoryForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText,
      "accountId" -> of[Long]
    )(Category.apply)(Category.unapply)
  )

  def newCategory(implicit token: String) = Action {
    val account = Account.findByToken(token).get
    Ok(views.html.Categories.newCategory(categoryForm.bind(Map("accountId" -> account.id.get.toString()))))
  }

  def createCategory(implicit token: String) = Action { implicit request =>

    categoryForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Categories.newCategory(errors))
      },
      category => {
        Category.create(category)
        Redirect(routes.Budgets.accountIndex(token))
      }
    )

  }

  def editCategory(implicit token: String, id: Long) = Action { implicit request =>
    val category = Category.findById(id).get
    Ok(views.html.Categories.editCategory(category, categoryForm.fill(category)))
  }

  def updateCategory(implicit token: String, id: Long) = Action { implicit request =>

    val category = Category.findById(id).get

    categoryForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Categories.editCategory(category, errors))
      },
      category => {
        Category.update(id, category)
        Redirect(routes.Budgets.accountIndex(token))
      }
    )

  }

}
