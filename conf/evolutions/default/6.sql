
# --- !Ups

-- Nueva tabla con usuarios

ALTER TABLE task ADD categoria varchar(255);
ALTER TABLE task ADD constraint fk_task_category FOREIGN KEY (categoria) REFERENCES category (nombre);



# --- !Downs
ALTER TABLE task DROP category;
