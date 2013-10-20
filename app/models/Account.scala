package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import scala.util.Random

case class Account(id: Pk[Long], token: String, monthlyIncome: Double)

object Account {

  val accountParser = {
     get[Pk[Long]]("id") ~
     get[String]("token") ~
     get[Double]("monthly_income") map {
      case id~token~monthlyIncome => Account(id, token, monthlyIncome)
    }
  }

  def generateToken: String = {
    val length = 8
    val rand = new Random()
    val seed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    var token = new String()
    for (c <- 0 until 8) {
      token = token :+ seed.charAt(rand.nextInt(seed.length))
    }
    Account.findByToken(token) match {
      case None => token
      case Some(m) => Account.generateToken
    }
  }

  def findByToken(token: String): Option[Account] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM accounts WHERE token = {token}").on(
      'token -> token
    ).as(accountParser.singleOpt)
  }

  def create(account: Account) {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO accounts (token, monthly_income) VALUES ({token}, {monthlyIncome})").on(
        'token -> account.token,
        'monthlyIncome -> account.monthlyIncome
      ).executeUpdate()
    }
  }

  def getIdFromToken(token: String): Long = {
    Account.findByToken(token).get.id.get
  }

}
