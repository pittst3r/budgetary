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

  def selectOptionSeq(): Seq[(String, String)] = {
    val categories = Category.all()
    categories.foldLeft(Seq[(String, String)]()) { (z, cur) =>
      z :+ (cur.id.get.toString(), cur.name)
    }
  }

  def create(category: Category) {
    DB.withConnection { implicit connection =>
      SQL("INSERT INTO categories (name) VALUES ({name})").on(
        'name -> category.name
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
