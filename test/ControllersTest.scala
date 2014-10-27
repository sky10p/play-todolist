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
  }
}