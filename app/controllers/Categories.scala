package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Category
import anorm.{Pk, NotAssigned}

object Categories extends Controller {

  val categoryForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "name" -> nonEmptyText
    )(Category.apply)(Category.unapply)
  )

  def newCategory = Action {
    Ok(views.html.Categories.newCategory(categoryForm))
  }

  def createCategory = Action { implicit request =>

    categoryForm.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.Categories.newCategory(errors))
      },
      category => {
        Category.create(category)
        Redirect(routes.Budgets.index)
      }
    )

  }

  def destroyCategory(id: Long) = Action { implicit request =>

    Category.destroy(id) match {
      case None => BadRequest
      case Some(category) => Ok
    }

  }

}
