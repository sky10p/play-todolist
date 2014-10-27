
# --- !Ups

-- Añadimos algunos datos de prueba con el usuario an´onimo

INSERT INTO task (label, task_owner) VALUES ('Terminar la feature 2', 'anonymous');
INSERT INTO task (label, task_owner) VALUES ('Ver el piloto de Flash', 'anonymous');

-- Añadimos algunos usuarios de prueba

INSERT INTO user_task (login) VALUES ('juan.perez');
INSERT INTO user_task (login) VALUES ('ana.garcia');

-- Añadimos algunas tareas en los usuarios anteriores


INSERT INTO task (label, task_owner) VALUES ('Preparar examen de fisica', 'juan.perez');
INSERT INTO task (label, task_owner, deadline) VALUES ('Examen de ingles', 'juan.perez', '2014-10-22');
INSERT INTO task (label, task_owner, deadline) VALUES ('Examen de valenciano', 'juan.perez', '2014-10-25');
INSERT INTO task (label, task_owner, deadline) VALUES ('Estudiar filosofia', 'ana.garcia', '2014-10-15');

# --- !Downs

DELETE FROM task
DELETE FROM user_task
