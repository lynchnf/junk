# Junk
Junk Spring MVC application.

DEVELOPMENT

4. Create database and user.
  - `mysql --user=root --password=********`
  - `create database junkdb;`
  - `create user junkuser identified by 'swordfish';`
  - `grant all on junkdb.* to junkuser;`
  - `exit`

Dump MySQL with the following command:
  - `mysqldump --user=junkuser --password=swordfish --skip-extended-insert --complete-insert --no-create-info --skip-add-locks junkdb > import.sql`
