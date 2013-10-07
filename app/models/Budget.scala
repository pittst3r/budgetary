package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Budget(name: String, amount: Double)

object Budget {

  val budgetParser = {
    get[String]("name") ~
    get[Double]("amount") map {
      case name~amount => Budget(name, amount)
    }
  }

  def all(): List[Budget] = DB.withConnection { implicit c =>
    SQL("SELECT * FROM budgets").as(budgetParser *)
  }

  def create(name: String, amount: Double) {
    DB.withConnection { implicit c =>
      SQL("INSERT INTO budgets (name, amount) VALUES ({name}, {amount})").on(
        ('name -> name),
        ('amount -> amount)
      ).executeUpdate()
    }
  }

  def update(name: String, amount: Double) {}

  def delete(id: Long) {}

}
