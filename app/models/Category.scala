package models
import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import java.util.Date

case class Category(id: Long, nombre: String, usuario: String)

object Category{
  
  val category = {
      get[Long]("id") ~ 
      get[String]("nombre") ~ 
      get[String]("usuario") map {
         case id~nombre~usuario => Category(id, nombre, usuario)
      }
   }
  
  
  def create(login: String, nombre: String):  Long = {
      DB.withConnection { implicit c =>
         val id: Option[Long]  = 
            SQL("insert into category (usuario, nombre) values ({usuario}, {nombre})").on(
               'usuario -> login,
               'nombre -> nombre
            ).executeInsert()

         //Devolvemos -1 si el insert devuelve None
         id.getOrElse(-1)
     }
   }

  def delete(categoriaId: Long): Long = {
    return -1
  }

  def findById(id: Long): Option[Category] = {
      DB.withConnection { implicit connection =>
         SQL("select * from category where id = {id}").on('id -> id).as(Category.category.singleOpt)
      }
   }

  
}