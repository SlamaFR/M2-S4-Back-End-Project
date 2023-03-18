-- Tags
INSERT INTO "tag" ("id", "name")
SELECT X'00000000000000000000000000000000', 'Spring'
WHERE NOT EXISTS(SELECT * FROM "tag" WHERE "name" = 'Spring');
INSERT INTO "tag" ("id", "name")
SELECT X'00000000000000000000000000000001', 'Kotlin'
WHERE NOT EXISTS(SELECT * FROM "tag" WHERE "name" = 'Kotlin');
INSERT INTO "tag" ("id", "name")
SELECT X'00000000000000000000000000000002', 'Java'
WHERE NOT EXISTS(SELECT * FROM "tag" WHERE "name" = 'Java');
INSERT INTO "tag" ("id", "name")
SELECT X'00000000000000000000000000000003', 'Web Application'
WHERE NOT EXISTS(SELECT * FROM "tag" WHERE "name" = 'Web Application');

-- Users
INSERT INTO "user" ("id", "username", "password")
SELECT X'00000000000000000000000000000000', 'arnaud', '$2b$12$q8pUtc8nWsGKAI4zO96k9.oZJaOH3SrVJFE8W9PjohTEWbYGSGNM6'
WHERE NOT EXISTS(SELECT * FROM "user" WHERE "username" = 'arnaud');
INSERT INTO "user" ("id", "username", "password")
SELECT X'00000000000000000000000000000001', 'admin', '$2b$12$uyby7879Sa9XoHooubGuveStJhRBudY2pu/l7w7tH46DzDvdvd3/C'
WHERE NOT EXISTS(SELECT * FROM "user" WHERE "username" = 'admin');


