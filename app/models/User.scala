package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._

object User {

   def exists(login: String): Boolean = DB.withConnection { implicit c =>
      SQL("select count(*) from user_task where login = {login}").on(
          'login -> login).as(scalar[Long].single) == 1
   }
}