package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import scala.util.Random

case class Account(id: Pk[Long], token: String)

object Account {

  val accountParser = {
     get[Pk[Long]]("id") ~
     get[String]("token") map {
      case id~token => Account(id, token)
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
      SQL("INSERT INTO accounts (token) VALUES ({token})").on(
        'token -> account.token
      ).executeUpdate()
    }
  }

}
