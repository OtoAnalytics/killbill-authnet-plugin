_Generating database objects using JOOQ_

- This should be run whenever the database schema changes.
- It will generate ORM for whatever tables are specified in <includes> tag in library.xml
- Below is the command to run the generator.  Needs to be pointed to jooq and mysql/postgres libraries.
- Should probably be moved into a maven task/step.

If you have the jooq and mysql/postgres libraries installed in Maven's repository:

java -classpath ~/.m2/repository/org/jooq/jooq/3.6.2/jooq-3.6.2.jar:~/.m2/repository/org/jooq/jooq-meta/3.6.2/jooq-meta-3.6.2.jar:~/.m2/repository/org/jooq/jooq-codegen/3.6.2/jooq-codegen-3.6.2.jar:~/.m2/repository/org/postgresql/postgresql/9.4-1202-jdbc42/postgresql-9.4-1205-jdbc42.jar:. org.jooq.util.GenerationTool library.xml

Or if they are only available locally:

java -classpath lib/jooq-3.6.2.jar:lib/jooq-meta-3.6.2.jar:lib/jooq-codegen-3.6.2.jar:lib/mysql-connector-java-5.1.36.jar:. org.jooq.util.GenerationTool library.xml


