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
    Ok(views.html.index(Task.all(), taskForm))
  }

  def tasksView(id: Long) = Action {
    val json = Json.toJson(Task.read(id))
    Ok(json)
  }

  def newTask = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => BadRequest(views.html.index(Task.all(), errors)),
      label => {
        Task.create(label)
        Redirect(routes.Application.tasks)
      })
  }

  def deleteTask(id: Long) = Action {
    Task.delete(id)
    Redirect(routes.Application.tasks)
  }

}