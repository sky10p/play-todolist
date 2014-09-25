# Tasks schema
 
# --- !Ups

CREATE TABLE task_user(
	nombre varchar(255)primary key);

CREATE SEQUENCE task_id_seq;
CREATE TABLE task (
    id integer NOT NULL DEFAULT nextval('task_id_seq'),
    login varchar(255) references task_user(nombre),
    label varchar(255),
    fecha	timestamp
);

INSERT INTO task_user (nombre) values ('anonimo');
INSERT INTO task_user (nombre) values ('pablo');
INSERT INTO task_user (nombre) values ('antonio');
INSERT INTO task_user (nombre) values ('nuria');
 
# --- !Downs
 
DROP TABLE task;
DROP TABLE task_user;
DROP SEQUENCE task_id_seq;