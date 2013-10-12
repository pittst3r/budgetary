package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Budget(id: Pk[Long], name: String, amount: Double, category_id: Option[Long])

object Budget {

  val budgetParser = {
    get[Pk[Long]]("id") ~
    get[String]("name") ~
    get[Double]("amount") ~
    get[Option[Long]]("category_id") map {
      case id~name~amount~category_id => Budget(id, name, amount, category_id)
    }
  }

  def all(): List[Budget] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM budgets").as(budgetParser *)
  }

  def findById(id: Long): Option[Budget] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM budgets WHERE id = {id}").on(
      'id -> id
    ).as(budgetParser.singleOpt)
  }

  def create(budget: Budget) {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO budgets (name, amount, category_id) VALUES ({name}, {amount}, {category_id})").on(
        'name -> budget.name,
        'amount -> budget.amount,
        'category_id -> budget.category_id
      ).executeUpdate()
    }
  }

  def update(id: Long, budget: Budget) {
    DB.withConnection { implicit connection =>
      SQL("UPDATE budgets SET name = {name}, amount = {amount}, category_id = {category_id} WHERE id = {id}").on(
        'id -> id,
        'name -> budget.name,
        'amount -> budget.amount,
        'category_id -> budget.category_id
      ).executeUpdate()
    }
  }

  def destroy(id: Long): Option[Budget] = {
    val budget = Budget.findById(id)
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM budgets WHERE id = {id}").on('id -> id).executeUpdate()
    } match {
      case 0 => None
      case 1 => budget
    }
  }

  def amountInCurrency(amount: Double): String = "%.2f".format(amount)

}
