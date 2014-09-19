package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Task(id: Long, label: String)

object Task{

	val task = {
			get[Long]("id") ~ 
			get[String]("label") map {
			case id~label => Task(id, label)
			}
	}

	def find(login:String): List[Task] = DB.withConnection { implicit c =>
	SQL("select * from task where login={login}").on(
      'login->login).as(task *)
	}

	def create(label: String, login: String) :Option[Long]={
    val id:Option[Long] =	DB.withConnection { implicit c =>
		SQL("insert into task (label, login) values ({label},{login})").on(
				'label -> label,
        'login -> login
				).executeInsert()
		}
    id
  }
	

	def delete(id: Long) :Int={
		DB.withConnection { implicit c =>
      val rowsDeleted=SQL("delete from task where id = {id}").on(
				'id -> id
				).executeUpdate()
        rowsDeleted
		}
	}
  
  def read(id:Long):List[Task]=DB.withConnection{ implicit c =>
      SQL("select * from task where id={id}").on(
        'id -> id
        ).as(task *)
      
  }
}