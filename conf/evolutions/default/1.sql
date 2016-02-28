# --- !Ups

create table "user" ("userID" VARCHAR NOT NULL PRIMARY KEY,"firstName" VARCHAR,"lastName" VARCHAR,"fullName" VARCHAR,"email" VARCHAR,"avatarURL" VARCHAR);
create table "logininfo" ("id" bigserial NOT NULL PRIMARY KEY,"providerID" VARCHAR NOT NULL,"providerKey" VARCHAR NOT NULL);
create table "userlogininfo" ("userID" VARCHAR NOT NULL,"loginInfoId" BIGINT NOT NULL);
create table "passwordinfo" ("hasher" VARCHAR NOT NULL,"password" VARCHAR NOT NULL,"salt" VARCHAR,"loginInfoId" BIGINT NOT NULL);
create table "oauth1info" ("id" bigserial NOT NULL PRIMARY KEY,"token" VARCHAR NOT NULL,"secret" VARCHAR NOT NULL,"loginInfoId" BIGINT NOT NULL);
create table "oauth2info" ("id" bigserial NOT NULL PRIMARY KEY,"accesstoken" VARCHAR NOT NULL,"tokentype" VARCHAR,"expiresin" INTEGER,"refreshtoken" VARCHAR,"logininfoid" BIGINT NOT NULL);
create table "openidinfo" ("id" VARCHAR NOT NULL PRIMARY KEY,"logininfoid" BIGINT NOT NULL);
create table "openidattributes" ("id" VARCHAR NOT NULL,"key" VARCHAR NOT NULL,"value" VARCHAR NOT NULL);


# --- !Downs

drop table "openidattributes";
drop table "openidinfo";
drop table "oauth2info";
drop table "oauth1info";
drop table "passwordinfo";
drop table "userlogininfo";
drop table "logininfo";
drop table "user";