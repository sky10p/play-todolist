#Practica 1 *v0.1* 
Documentación de la práctica 1.

Lo primero fue preparar el proyecto para usar eclipse, para ello añadí algunos directorios al .gitignore para que no se
tuvieran en cuenta en los commit.

- .cache
- bin

Una vez hecho esto empecé a implementar las nuevas características.
##Feature1##
Como ya no se trabaja con interfaz gráfica, lo primero fue editar el fichero index.scala.html para que no diera problemas con los nuevos cambios, comentando las funciones.

Se implementa las nuevas rutas para consultar, crear y listar una tarea, añadiendo las siguientes lineas al fichero **/conf/routes**.

```
GET     /tasks                  controllers.Application.tasks(login: String="anonimo")
GET		/tasks/:id				controllers.Application.tasksView(id:Long)

POST    /tasks                  controllers.Application.newTask(login:String="anonimo")
POST	/:login/tasks			controllers.Application.newTask(login:String)

DELETE    /tasks/:id   controllers.Application.deleteTask(id:Long)
```
###Crear tarea
Se permite crear una nueva tarea usando

`POST http://localhost:9000/tasks?label=pues+eso`

###Visualizar tareas
`GET http://localhost:9000/tasks`

Recibimos el json correspondiente

`[{"id":1,"label":"pues eso"}]`

###Consultar una tarea determinada
`Get http://localhost:9000/tasks/2`

###Borrar una tarea
`DELETE /tasks/id`

##Feature2

Para permitir añadir el usuario al que envia o visualiza las tareas añadimos el campo login en la base de datos 

```
CREATE SEQUENCE task_id_seq;
CREATE TABLE task (
    id integer NOT NULL DEFAULT nextval('task_id_seq'),
    login varchar(255),
    label varchar(255),
);
```

y añadimos las rutas correspondientes al fichero routes

```
GET     /tasks                  controllers.Application.tasks(login: String="anonimo")

GET		/:login/tasks			controllers.Application.tasks(login:String)

POST    /tasks                  controllers.Application.newTask(login:String="anonimo")

POST	/:login/tasks			controllers.Application.newTask(login:String)
```
Las rutas sin login le ponemos *anonimo* de login.

Para controlar que el usuario es valido usamos este codigo: 
```
 val firstRow = SQL("select count(*) as c from task_user where nombre={nombre}").on('nombre -> login).apply().head
    val users_count = firstRow[Long]("c")
    if (users_count != 0) {
      Some(SQL("select * from task where login={login}").on(
        'login -> login).as(task *))
    } else {
      return None
    }
```
es decir se comprueba que hay usuarios en la base task_user y si hay hace el select correspondiente si no devuelve un Option vacio, luego se manda la respuesta usando esto de la siguiente manera:

```
val tasks = Task.create(login, task)
        if (tasks == None) {
          NotFound("No se ha encontrado el usuario")
        } else {
          val id = tasks
          val json = Json.toJson(Task.read(id.get))
          Created(json)
        }
```

si devuelve un option vacio devuelve un status de Not Found y si no lee los valores de ese id y lo crea.

##Feature 3

Para la implementación de las fechas he añadido dos funciones:

-crear una tarea **(ahora se puede añadir una fecha)**

-Modificar la fecha de una tarea

Para ello añadimos el campo fecha en la base de datos:

`fecha	timestamp`

añadimos la ruta para la modificacion de la fecha.

```
PUT		/tasks/:id				controllers.Application.modifyDate(id:Long)
```
ahora podemos crear una tarea con fecha de la siguiente manera*(podemos hacerlo con usuarios anonimos o usuarios con login)*:

`POST /tasks?label=lo+que+sea&fecha=12/08/2013`

para modificar una fecha de una tarea ya creada usamos la siguiente funcion, imaginemos que queremos cambiar la fecha de la tarea 2

`PUT /tasks/2?fecha=15/09/2013`