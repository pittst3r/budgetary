package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Category(id: Pk[Long], name: String)

object Category {

  val categoryParser = {
    get[Pk[Long]]("id") ~
    get[String]("name") map {
      case id~name => Category(id, name)
    }
  }

  def all(): List[Category] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM categories").as(categoryParser *)
  }

  def allInAccount(implicit token: String): List[Category] = DB.withConnection { implicit connection =>
    val account = Account.findByToken(token)
    SQL("SELECT * FROM categories WHERE account_id = {accountId}").on(
      'accountId -> account.get.id.get
    ).as(categoryParser *)
  }

  def budgets(categoryId: Long): List[Budget] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM budgets WHERE category_id = {categoryId}").on(
      'categoryId -> categoryId
    ).as(Budget.budgetParser *)
  }

  def findById(id: Long): Option[Category] = DB.withConnection { implicit connection =>
    SQL("SELECT * FROM categories WHERE id = {id}").on(
      'id -> id
    ).as(categoryParser.singleOpt)
  }

  def selectOptionSeq(implicit token: String): Seq[(String, String)] = {
    val categories = Category.allInAccount
    categories.foldLeft(Seq[(String, String)]()) { (z, cur) =>
      z :+ (cur.id.get.toString(), cur.name)
    }
  }

  def create(category: Category)(implicit token: String) {
    val account = Account.findByToken(token)
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO categories (name, account_id) VALUES ({name}, {accountId})").on(
        'name -> category.name,
        'accountId -> account.get.id.get
      ).executeUpdate()
    }
  }

  def update(id: Long, category: Category) {
    DB.withConnection { implicit connection =>
      SQL("UPDATE categories SET name = {name} WHERE id = {id}").on(
        'id -> id,
        'name -> category.name
      ).executeUpdate()
    }
  }

  def destroy(id: Long): Option[Category] = {
    val category = Category.findById(id)
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM categories WHERE id = {id}").on('id -> id).executeUpdate()
    } match {
      case 0 => None
      case 1 => category
    }
  }

}
