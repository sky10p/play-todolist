
# --- !Ups

-- Nueva tabla con usuarios

CREATE TABLE user_task (
   login varchar(255) NOT NULL,
   first_name varchar(255),
   last_name varchar(255),
   e_mail varchar(255),
   PRIMARY KEY (login)
);

-- Añadimos clave ajena en la tabla de tareas
 
ALTER TABLE task ADD task_owner varchar(255);
ALTER TABLE task ADD constraint fk_task_user_1 FOREIGN KEY (task_owner) REFERENCES user_task (login);

-- Añadimos el usuario anonimo

INSERT INTO user_task (login) VALUES ('anonymous');

# --- !Downs
DROP TABLE user_task;
ALTER TABLE task DROP user_task;
