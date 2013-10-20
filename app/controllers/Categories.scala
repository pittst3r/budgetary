package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.{CategoryWithoutAccount, Category, Account}
import anorm.{Pk, NotAssigned}
import play.api.data.format.Formats._

object Categories extends Controller {

  val categoryForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText
    )(CategoryWithoutAccount.apply)(CategoryWithoutAccount.unapply)
  )

  def newCategory(implicit token: String) = Action {
    val account = Account.findByToken(token).get
    Ok(views.html.Categories.newCategory(categoryForm))
  }

  def createCategory(implicit token: String) = Action { implicit request =>

    categoryForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Categories.newCategory(errors))
      },
      categoryWithoutAccount => {
        Category.create(categoryWithoutAccount.toCategory(Account.getIdFromToken(token)))
        Redirect(routes.Budgets.accountIndex(token))
      }
    )

  }

  def editCategory(implicit token: String, id: Long) = Action { implicit request =>
    val category = Category.findById(id).get
    Ok(views.html.Categories.editCategory(category, categoryForm.fill(category.withoutAccount)))
  }

  def updateCategory(implicit token: String, id: Long) = Action { implicit request =>

    categoryForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Categories.editCategory(Category.findById(id).get, errors))
      },
      category => {
        Category.update(id, category.name)
        Redirect(routes.Budgets.accountIndex(token))
      }
    )

  }

}
