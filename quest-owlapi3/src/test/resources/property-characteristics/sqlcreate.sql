CREATE TABLE "R"
(
  "id" INT NOT NULL,
  "p1" VARCHAR(10),
  "p2" VARCHAR(10),
  CONSTRAINT r_pk PRIMARY KEY ("id" )
);

INSERT INTO "R" VALUES (1, 'A', 'p');
INSERT INTO "R" VALUES (2, 'B', 'q');
INSERT INTO "R" VALUES (3, 'C', 'r');

