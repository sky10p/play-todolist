package controllers

import play.api._
import java.util.Date
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Task
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Application extends Controller {

  val taskForm = Form(
    mapping(
      "id" -> ignored(None: Option[Long]),
      "label" -> nonEmptyText,
      "fecha" -> optional(date("dd/MM/yyyy")))(Task.apply)(Task.unapply))

  val fechaForm = Form(
    "fecha" -> optional(date("dd/MM/yyyy")))

  implicit val taskWrites: Writes[Task] = (
    (JsPath \ "id").write[Option[Long]] and
    (JsPath \ "label").write[String] and
    (JsPath \ "fecha").write[Option[Date]])(unlift(Task.unapply))

  def index = Action {
    Redirect(routes.Application.tasks("anonimo"))
  }

  def tasks(login: String) = Action {
    val tasks = Task.find(login)
    if (tasks == None) {
      NotFound("No se ha encontrado el usuario")
    } else {
      val json = Json.toJson(tasks.get)
      Ok(json)
    }

  }

  def tasksView(id: Long) = Action {
    val json = Json.toJson(Task.read(id))
    Ok(json)
  }

  def newTask(login: String) = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest("No has dado los parametros correctos"),
      task => {
        val tasks = Task.create(login, task)
        if (tasks == None) {
          NotFound("No se ha encontrado el usuario")
        } else {
          val id = tasks
          val json = Json.toJson(Task.read(id.get))
          Created(json)
        }

      })
  }

  def modifyDate(id: Long) = Action { implicit request =>
    fechaForm.bindFromRequest.fold(
      errors => BadRequest("No has dado los parametros correctos"),
      fecha => {
        val json = Json.toJson(Task.modifyDate(id, fecha))
        Ok(json)

      })
  }

  def deleteTask(id: Long) = Action {
    val rowsDeleted = Task.delete(id)
    if (rowsDeleted >= 1) {
      Ok("Borrado correctamente")
    } else {
      NotFound("La tarea no existe")
    }

  }

}