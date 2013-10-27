package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Allocation(id: Pk[Long], name: String, amount: Double, accountId: Long)

case class AllocationWithoutAccount(id: Pk[Long], name: String, amount: Double) {

  def toAllocation(accountId: Long): Allocation = Allocation(id, name, amount, accountId)

}

object Allocation {

  val allocationParser = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[Double]("amount") ~
      get[Long]("account_id") map {
      case id~name~amount~accountId => Allocation(id, name, amount, accountId)
    }
  }

  def allInAccount(accountId: Long): List[Allocation] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM allocations WHERE account_id = {accountId}").on(
      'accountId -> accountId
    ).as(allocationParser *)
  }

  def allInAccount(token: String): List[Allocation] = DB.withConnection { implicit connection =>
    val account = Account.findByToken(token).get
    allInAccount(account.id.get)
  }

  def findById(id: Long): Option[Allocation] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM allocations WHERE id = {id}").on(
      'id -> id
    ).as(allocationParser.singleOpt)
  }

  def create(allocation: Allocation) {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO allocations (name, amount, account_id) VALUES ({name}, {amount}, {accountId})").on(
        'name -> allocation.name,
        'amount -> allocation.amount,
        'accountId -> allocation.accountId
      ).executeUpdate()
    }
  }

  def update(id: Long, allocation: Allocation) {
    DB.withConnection { implicit connection =>
      SQL("UPDATE allocations SET name = {name}, amount = {amount} WHERE id = {id}").on(
        'id -> id,
        'name -> allocation.name,
        'amount -> allocation.amount
      ).executeUpdate()
    }
  }

  def destroy(id: Long): Option[Allocation] = {
    val allocation = Allocation.findById(id)
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM allocations WHERE id = {id}").on('id -> id).executeUpdate()
    } match {
      case 0 => None
      case 1 => allocation
    }
  }

  def accountTotal(accountId: Long): Double = {
    allInAccount(accountId).foldLeft(0.toDouble) { (z, a) =>
      z + a.amount
    }
  }

  def accountTotal(token: String): Double = {
    val accountId = Account.findByToken(token).get.id.get
    allInAccount(accountId).foldLeft(0.toDouble) { (z, a) =>
      z + a.amount
    }
  }

}
