
# --- !Ups

-- Nueva tabla con usuarios

ALTER TABLE task ADD categoria varchar(255);
ALTER TABLE task ADD constraint fk_task_category FOREIGN KEY (task_owner,categoria) REFERENCES category (usuario,nombre);



# --- !Downs
ALTER TABLE task DROP category;
