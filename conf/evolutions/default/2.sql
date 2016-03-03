# --- !Ups
create table "fill_attributes" ("id" bigserial NOT NULL PRIMARY KEY,
	"fill_id" BIGINT NOT NULL,
	"attribute" VARCHAR NOT NULL,
	"value" VARCHAR NOT NULL);

create table "fill" ("id" bigserial NOT NULL PRIMARY KEY,
	"phone" VARCHAR NOT NULL,
	"registered" BOOLEAN NOT NULL,
	"filled" BOOLEAN NOT NULL,
	"filledCorrect" BOOLEAN NOT NULL,
	"signRequested" BOOLEAN NOT NULL,
	"signMarked" BOOLEAN NOT NULL,
	"smsCode" VARCHAR NOT NULL,
	"signCompleted" BOOLEAN NOT NULL);


# --- !Downs

drop table "fill_attributes";
drop table "fill";
