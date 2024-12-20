create table TASK(
  ID int not null AUTO_INCREMENT,
  NAME varchar(100) not null,
  DESCRIPTION varchar(200) not null,
  STATUS int default 1,
  PRIMARY KEY ( ID )
);