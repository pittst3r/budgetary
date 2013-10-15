package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Account
import anorm.{Pk, NotAssigned}
import play.api.data.format.Formats._

object Accounts extends Controller {

  val accountForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "token" -> text
    )(Account.apply)(Account.unapply)
  )

  def newAccount = Action {
    Ok(views.html.Accounts.newAccount(accountForm)(Account.generateToken))
  }

  def createAccount = Action { implicit request =>

    accountForm.bindFromRequest.fold(
      errors => BadRequest,
      account => {
        Account.create(account)
        Redirect(routes.Budgets.accountIndex(account.token))
      }
    )

  }

}
