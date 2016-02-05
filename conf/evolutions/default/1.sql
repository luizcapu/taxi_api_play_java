# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table driver (
  id                        integer not null,
  latitude                  double not null,
  longitude                 double not null,
  available                 boolean not null,
  constraint pk_driver primary key (id))
;

create sequence driver_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists driver;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists driver_seq;

