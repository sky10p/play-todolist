package models

import anorm._
import java.util.Date
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Task(id: Option[Long], label: String, fecha: Option[Date])

object Task {

  val task = {
    get[Option[Long]]("id") ~
      get[String]("label") ~
      get[Option[Date]]("fecha") map {
        case id ~ label ~ fecha => Task(id, label, fecha)
      }
  }

  def find(login: String): Option[List[Task]] = DB.withConnection { implicit c =>
    val firstRow = SQL("select count(*) as c from task_user where nombre={nombre}").on('nombre -> login).apply().head
    val users_count = firstRow[Long]("c")
    if (users_count != 0) {
      Some(SQL("select * from task where login={login}").on(
        'login -> login).as(task *))
    } else {
      return None
    }
  }

  def create(login: String, task: Task): Option[Long] = {

    val id: Option[Long] = DB.withConnection { implicit c =>
      val firstRow = SQL("select count(*) as c from task_user where nombre={nombre}").on('nombre -> login).apply().head
      val users_count = firstRow[Long]("c")

      if (users_count == 0) {
        None
      } else {
        SQL("insert into task (label, login, fecha) values ({label},{login},{fecha})").on(
          'label -> task.label,
          'login -> login,
          'fecha -> task.fecha).executeInsert()
      }
    }

    id

  }

  def delete(id: Long): Int = {
    DB.withConnection { implicit c =>
      val rowsDeleted = SQL("delete from task where id = {id}").on(
        'id -> id).executeUpdate()
      rowsDeleted
    }
  }

  def read(id: Long): List[Task] = DB.withConnection { implicit c =>
    SQL("select * from task where id={id}").on(
      'id -> id).as(task *)

  }

  def modifyDate(id: Long, fecha: Option[Date]): Int = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update task set fecha={fecha} where id = {id}").on(
        'id -> id,
        'fecha -> fecha).executeUpdate()
      rowsUpdated
    }
  }
}