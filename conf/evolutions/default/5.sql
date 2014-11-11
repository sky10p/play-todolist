
# --- !Ups

-- Nueva tabla con usuarios

CREATE TABLE category (
   id integer NOT NULL DEFAULT nextval('task_id_seq'),
   usuario varchar(255) NOT NULL,
   nombre varchar(255) NOT NULL,
   PRIMARY KEY (usuario, nombre),
 constraint fk_category_user FOREIGN KEY (usuario) REFERENCES user_task(login)
);




# --- !Downs
DROP TABLE user_task;
ALTER TABLE task DROP user_task;
ALTER TABLE category DROP user_task;