package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import models.User
import models.Category
import java.util.Date
import java.text.SimpleDateFormat
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class TaskData(label: String, user: String, deadline: Option[Date])
case class CategoryData(usuario: String, nombre: String)

object Application extends Controller {

   val dateWrite = Writes.dateWrites("yyyy-MM-dd")
   val formatter = new SimpleDateFormat("yyyy-MM-dd")

   implicit val locationWrites: Writes[Task] = (
      (JsPath \ "id").write[Long] and
      (JsPath \ "label").write[String] and
      (JsPath \ "categoria").writeNullable[String] and
      (JsPath \ "taskOwner").write[String] and
      (JsPath \ "deadline").writeNullable[Date](dateWrite)
   )(unlift(Task.unapply))
   
   implicit val locationCategory: Writes[Category]=(
       (JsPath \ "id").write[Long] and
       (JsPath \ "nombre").write[String] and
       (JsPath \ "usuario").write[String]
       )(unlift(Category.unapply))

   val taskForm = Form(
      mapping( 
         "label" -> nonEmptyText,
         "usuario" -> text,
         "deadline" -> optional(date("yyyy-MM-dd"))
      )(TaskData.apply)(TaskData.unapply)
   )
   
   val categoryForm = Form(
      mapping( 
         "usuario" -> text,
         "nombre" -> text
      )(CategoryData.apply)(CategoryData.unapply)
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
       taskData => if (User.exists(user)) {
                   val id: Long = Task.create(taskData.label, user, taskData.deadline)
                   val task = Task.findById(id)
                   Created(Json.toJson(Task.findById(id)))
                }
                else BadRequest("Error: No existe el propietario de la tarea: " + user)
     )
   }

   def deleteTask(id: Long) = Action {
     if (Task.delete(id)) Ok else NotFound
   }
   
   def newCategory(user: String)=Action{
     implicit request =>
     categoryForm.bindFromRequest.fold(
       errors => BadRequest("Error en la peticion"),
       categoryData => if (User.exists(user)) {
                   
                   val id: Long = Category.create(user, categoryData.nombre)
                   val categoria = Category.findById(id)
                   Created(Json.toJson(categoria))
                }
                else BadRequest("Error: No existe el propietario de la tarea: " + user)
     )
   }
   
   def addTasktoCategory(login: String, id: Long, categoriaId: String)=Action{
     
    val id_task: Long= Task.toCategory(id, categoriaId)
    val task= Task.findById(id_task)
     Ok(Json.toJson(task))
   }
   
   def listPerCategory(login: String, categoria: String)=Action{
     Ok(Json.toJson(Task.findByCategory(login, categoria)))
   }
}