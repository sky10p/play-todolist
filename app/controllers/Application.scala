package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Application extends Controller {

  val taskForm = Form(
    "label" -> nonEmptyText)

  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Long] and
    (JsPath \ "label").write[String])(unlift(Task.unapply))

  def index = Action {
    Redirect(routes.Application.tasks)
  }

  def tasks = Action {
    val json = Json.toJson(Task.all())
    Ok(json)
  }

  def tasksView(id: Long) = Action {
    val json = Json.toJson(Task.read(id))
    Ok(json)
  }

  def newTask = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest("No has dado los parametros correctos"),
      label => {
        val id = Task.create(label)
        val json = Json.toJson(Task.read(id.get))
        Created(json)
      })
  }

  def deleteTask(id: Long) = Action {
    val rowsDeleted=Task.delete(id)
    if(rowsDeleted>=1){
      Ok("Borrado correctamente")
    }else{
      NotFound("La tarea no existe")
    }
   
  }

}