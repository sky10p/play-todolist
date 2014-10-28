import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.{Json, JsValue}
import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.matcher._
import models.Task
import org.junit.runner.RunWith

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
    
    
  }
}