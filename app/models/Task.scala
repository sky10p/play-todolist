package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Task(id: Long, label: String, categoria: Option[String], taskOwner: String, deadline: Option[Date] = None)

object Task {

   val task = {
      get[Long]("id") ~ 
      get[String]("label") ~ 
      get[String]("task_owner") ~ 
      get[Option[String]]("categoria") ~
      get[Option[Date]]("deadline") map {
         case id~label~user~categoria~deadline => Task(id, label, categoria,user, deadline)
      }
   }
 
   def all(): List[Task] = DB.withConnection { implicit c =>
     SQL("select * from task").as(task *)
   }

   def all(user: String, dateStr: Option[Date] = None): List[Task] = DB.withConnection { implicit c =>
      dateStr match {
          case Some(date) => SQL("select * from task where task_owner = {user} and deadline = {date}").on (
                                 'user -> user,
                                 'date -> date
                              ).as(task *)
          case None => SQL("select * from task where task_owner = {user}").on (
                                 'user -> user
                              ).as(task *)
      }
   }

   def create(label: String, taskOwner: String, deadline: Option[Date] = None): Long = {
      DB.withConnection { implicit c =>
         val id: Option[Long]  = 
            SQL("insert into task (label, task_owner, deadline) values ({label}, {taskOwner},{deadline})").on(
               'label -> label,
               'taskOwner -> taskOwner,
               'deadline -> deadline
            ).executeInsert()

         //Devolvemos -1 si el insert devuelve None
         id.getOrElse(-1)
     }
   }

   def findById(id: Long): Option[Task] = {
      DB.withConnection { implicit connection =>
         SQL("select * from task where id = {id}").on('id -> id).as(Task.task.singleOpt)
      }
   }
   
   def findByCategory(login: String, categoria: String): List[Task]={
     DB.withConnection { implicit connection =>
         SQL("select * from task where task_owner = {usuario} and categoria= {categoria}").on(
             'usuario -> login, 'categoria -> categoria).as(task *)
      }
   }
   
   def toCategory(id: Long, categoryId: String): Long ={
     
    
     
     DB.withConnection { implicit connection =>
       val task_result: Long  = SQL("update task set categoria={category} where id = {id}").on('id -> id, 'category -> categoryId).executeUpdate()
     task_result match {
         case 1 => id
         case _ => -1
       }
     }
   }
   
   def delete(id: Long): Boolean = {
     DB.withConnection { implicit c =>
       val result: Int = SQL("delete from task where id = {id}").on(
         'id -> id
       ).executeUpdate()
       result match {
         case 1 => true
         case _ => false
       }
     }
   }
}