
# --- !Ups

-- Nueva tabla con usuarios

-- Añadimos fecha opcional en la tabla de tareas
 
ALTER TABLE task ADD deadline date;

# --- !Downs
ALTER TABLE task DROP deadline;
