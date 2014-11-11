#Practica 2 *v0.2* 
Documentación de la práctica 2.

Implementacion de los test de las diferentes caracteristicas
##Test de Prueba para explicacion##
Todos los test siguen ese esquema en el que se abre un FakeApplication para ejecutar las pruebas, se crea la tarea correspondiente, se prueba el enlace REST y se borra, luego se comprueba el json correspondiente para ver que tiene los valores correctos.

```
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
```

##TDD

La ultima parte de la practica consiste en usar la metodologia TDD para implementar categorias en las tareas, para ello hemos creado los test de crear, listar, y cambiar la categoria de una tarea.

Tras crear cada uno de los test, se debe pasar correctamente y refactorizar el codigo para conseguir que funcione, así como crear las bases de datos.

##Bases de datos

he echo algunas modificaciones para añadir las categorias y asignarselas a una tarea

```
CREATE TABLE category (
   id integer NOT NULL DEFAULT nextval('task_id_seq'),
   usuario varchar(255) NOT NULL,
   nombre varchar(255) NOT NULL,
   PRIMARY KEY (usuario, nombre),
 constraint fk_category_user FOREIGN KEY (usuario) REFERENCES user_task(login)
);

ALTER TABLE task ADD categoria varchar(255);
ALTER TABLE task ADD constraint fk_task_category FOREIGN KEY (categoria) REFERENCES category (nombre);


```

También he añadido las rutas nuevas REST para las categorias

```
POST	/:login/categorias			controllers.Application.newCategory(login: String)
PUT	/:login/:task/categoria/:categoriaId	controllers.Application.addTasktoCategory(login: String, task: Long, categoriaId: String)
GET /:login/:categoria/tasks		controllers.Application.listPerCategory(login: String, categoria: String)

```