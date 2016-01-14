# --- !Ups

/*
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def firstName = column[Option[String]]("FIRSTNAME")
  def lastName = column[Option[String]]("LASTNAME")
  def fullname = column[Option[String]]("FULLNAME")
  def email = column[Option[String]]("EMAIL")
  def avatarURL = column[Option[String]]("avatarURL")
  def providerID = column[String]("PROVIDER_ID")
  def providerKey = column[String]("PROVIDER_KEY")
*/
create table "users" ("ID" bigserial PRIMARY KEY ,
  "FIRSTNAME" VARCHAR(100),
  "LASTNAME" VARCHAR(100),
  "FULLNAME" VARCHAR(100),  
  "EMAIL" VARCHAR(250) UNIQUE,
  "avatarURL" VARCHAR(250),  
  "PROVIDER_ID" VARCHAR(250) NOT NULL,
  "PROVIDER_KEY" VARCHAR(250) NOT NULL,
  UNIQUE ("PROVIDER_ID","PROVIDER_KEY"));

create table "roles" (
  "ID" bigserial PRIMARY KEY ,
  "USER_ID" bigint REFERENCES "users",
  "ROLE" VARCHAR(30),
  UNIQUE ("ID","USER_ID","ROLE")
);
/*
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def userID = column[Long]("USER_ID")
  def role = column[String]("ROLE")
  def * = (id.?, userID, role) <>(DBRole.tupled, DBRole.unapply)

  def id = column[Long]("ID",O.PrimaryKey,O.AutoInc)
  def hasher = column[String]("HASHER")
  def password = column[String]("PASSWORD")
  def salt = column[Option[String]]("SALT")
  def userID = column[Long]("USER_ID")
  */
create table "password_infos" (
  "ID" bigserial PRIMARY KEY ,
  "HASHER" VARCHAR NOT NULL ,
  "PASSWORD" VARCHAR NOT NULL,
  "SALT" VARCHAR,
  "USER_ID" bigint REFERENCES "users"
);

create INDEX loginInfo_index ON "users" ("PROVIDER_ID","PROVIDER_KEY") ;

# --- !Downs
drop INDEX loginInfo_index;
drop TABLE "password_infos";
drop TABLE "roles";
drop table "users";