package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Budget(id: Pk[Long], name: String, amount: Double)

object Budget {

  val budgetParser = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~
    get[Double]("amount") map {
      case id~name~amount => Budget(id, name, amount)
    }
  }

  def all(): List[Budget] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM budgets").as(budgetParser *)
  }

  def findById(id: Long): Option[Budget] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM budgets WHERE id = {id}").on(
      'id -> id
    ).as(budgetParser.singleOpt)
  }

  def create(budget: Budget) {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO budgets (name, amount) VALUES ({name}, {amount})").on(
        'name -> budget.name,
        'amount -> budget.amount
      ).executeUpdate()
    }
  }

  def update(budget: Budget) {}

  def delete(budget: Budget) {}

  def amountInCurrency(amount: Double): String = "%.2f".format(amount)

}
