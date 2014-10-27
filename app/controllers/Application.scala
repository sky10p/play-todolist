package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import models.User
import java.util.Date
import java.text.SimpleDateFormat

// JSON

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class TaskData(label: String, user: String, deadline: Option[Date])

object Application extends Controller {

   val dateWrite = Writes.dateWrites("yyyy-MM-dd")
   val formatter = new SimpleDateFormat("yyyy-MM-dd")

   implicit val locationWrites: Writes[Task] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "label").write[String] and
      (JsPath \ "taskOwner").write[String] and
      (JsPath \ "deadline").writeNullable[Date](dateWrite)
   )(unlift(Task.unapply))

   val taskForm = Form(
      mapping( 
         "label" -> nonEmptyText,
         "usuario" -> nonEmptyText,
         "deadline" -> optional(date("yyyy-MM-dd"))
      )(TaskData.apply)(TaskData.unapply)
   )

   def index = Action {
      Ok(views.html.index(Task.all(), taskForm))
   }

   def tasks = Action {
      Ok(Json.toJson(Task.all("anonymous")))
   }

   def tasksUserToday(user: String) = tasksUser(user, Some(formatter.format(new Date)))

   def tasksUser(user: String, dateStr: Option[String]) = Action {
      if (User.exists(user)) {
         val date = dateStr match {
            case Some(dateStr) => Some(formatter.parse(dateStr))
            case None => None
         }
         Ok(Json.toJson(Task.all(user, date)))
      }
      else BadRequest("Error: No existe el propietario de la tarea: " + user)
   }

   def findTask(id: Long) = Action {
      val task:Option[Task] = Task.findById(id)
      task match {
         case Some(t) => Ok(Json.toJson(t))
         case None => NotFound
      }
   }

   def newTask = newTaskUser("anonymous")

   def newTaskUser(user: String) = Action { implicit request =>
     taskForm.bindFromRequest.fold(
       errors => BadRequest("Error en la peticion"),
       taskData => if (User.exists(taskData.user)) {
                   val id: Long = Task.create(taskData.label, taskData.user, taskData.deadline)
                   val task = Task.findById(id)
                   Created(Json.toJson(Task.findById(id)))
                }
                else BadRequest("Error: No existe el propietario de la tarea: " + taskData.user)
     )
   }

   def deleteTask(id: Long) = Action {
     if (Task.delete(id)) Ok else NotFound
   }
}