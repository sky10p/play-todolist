import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{Json, JsValue}
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._
import models.Task
import org.junit.runner.RunWith
import java.util.Date

@RunWith(classOf[JUnitRunner])
class ControllersTests extends Specification with JsonMatchers {
 
  "Feature 1(API Rest basica)" should {
 
    "devolver una tarea en formato JSON con un GET /tasks/<id>" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            val taskId = Task.create("prueba","anonymous")
            val Some(resultTask) = route(FakeRequest(GET, "/tasks/"+taskId))
            Task.delete(taskId)
 
            status(resultTask) must equalTo(OK)
            contentType(resultTask) must beSome.which(_ == "application/json")
 
            val resultJson: JsValue = contentAsJson(resultTask)
            val resultString = Json.stringify(resultJson) 
 
            resultString must /("id" -> taskId)
            resultString must /("label" -> "prueba")
            resultString must /("taskOwner" -> "anonymous")
         }
      }
    
    "devolver NotFound si el id de la tarea no existe o ha sido borrada" in {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            val taskId = Task.create("prueba","anonymous")
           
            Task.delete(taskId)
             val Some(resultTask) = route(FakeRequest(GET, "/tasks/"+taskId))
             
            status(resultTask) must equalTo(NOT_FOUND)
            
         }
    }
    
     "devolver el status created y la tarea creada con un POST /tasks" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label", "prueba"),("usuario",""),("deadline","")))
             
            status(resultTask) must equalTo(CREATED)
           
            contentType(resultTask) must beSome.which(_ == "application/json")
 
            val resultJson: JsValue = contentAsJson(resultTask)
            val resultString = Json.stringify(resultJson) 
 
            
            resultString must /("label" -> "prueba")
            resultString must /("taskOwner" -> "anonymous")
         }
    }
     
     "devolver NOT FOUND si se intenta borrar una tarea que no exista" in{
       running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(DELETE, "/tasks/2584"))
            status(resultTask) must equalTo(NOT_FOUND)
       }
     }
  }
  
  "Feature 2(Usuario Creador de la tarea" should {
    "devolver las tareas creadas de un usuario" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(GET, "/juan.perez/tasks"))
            status(resultTask) must equalTo(OK)
             
             contentType(resultTask) must beSome.which(_ == "application/json")
 
            val resultJson: JsValue = contentAsJson(resultTask)
            
            val resultString = Json.stringify(resultJson(0)) 
            
         
              
            resultString must /("taskOwner" -> "juan.perez")
            
       }
    }
    
    "crear una nueva tarea usando POST <login>/tasks" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(POST, "/juan.perez/tasks").withFormUrlEncodedBody(("label", "soy un usuario no anonimo"),("usuario",""),("deadline","")))
             
            status(resultTask) must equalTo(CREATED)
           
            contentType(resultTask) must beSome.which(_ == "application/json")
 
            val resultJson: JsValue = contentAsJson(resultTask)
            val resultString = Json.stringify(resultJson) 
 
            
            resultString must /("label" -> "soy un usuario no anonimo")
            resultString must /("taskOwner" -> "juan.perez")
         }
    }
    
    "crear una tarea con un usuario que no existe" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(POST, "/usuarioNoExiste/tasks").withFormUrlEncodedBody(("label", "soy un usuario no anonimo"),("usuario",""),("deadline","")))
             
            status(resultTask) must equalTo(BAD_REQUEST)
           
         }
    }
    
    
  }
  
  "Feature 3(Fecha en la tarea)" should{
    "poder crear tareas con una fecha en la que se termina la tarea" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label", "prueba"),("usuario",""),("deadline","2014-08-12")))
             
            status(resultTask) must equalTo(CREATED)
           
            contentType(resultTask) must beSome.which(_ == "application/json")
 
            val resultJson: JsValue = contentAsJson(resultTask)
            val resultString = Json.stringify(resultJson) 
 
            
            resultString must /("label" -> "prueba")
            resultString must /("taskOwner" -> "anonymous")
            resultString must /("deadline"->"2014-08-12")
         }
    }
    
    "devolver error en caso de que la fecha sea incorrecta" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
 
            
             val Some(resultTask) = route(FakeRequest(POST, "/tasks").withFormUrlEncodedBody(("label", "prueba"),("usuario",""),("deadline","2 DE MAYO")))
             
            status(resultTask) must equalTo(BAD_REQUEST)
           
          
         }
    }
    
    "Devolver las tareas de un usuario que terminan hoy" in{
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val fechaGuar=new Date
      val taskId = Task.create("fecha actual","anonymous", Some(fechaGuar))
            val Some(resultTask) = route(FakeRequest(GET, "/anonymous/tasks/today"))
            Task.delete(taskId)
             status(resultTask) must equalTo(OK)
           
            contentType(resultTask) must beSome.which(_ == "application/json")
 
            val resultJson: JsValue = contentAsJson(resultTask)
            val resultString = Json.stringify(resultJson(0)) 
 
            val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
            val fecha=format.format(fechaGuar)
            
            resultString must /("label" -> "fecha actual")
            resultString must /("taskOwner" -> "anonymous")
            resultString must /("deadline"->fecha)
            
            
      }
         
    }
    
  }
}