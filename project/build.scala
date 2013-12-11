import sbt._
import Keys._

object HbPlugin extends Build {

  val databaseConfig = settingKey[String]("The database environment.  We use this to determine the datbase properties file to grab login information from.")

  databaseConfig := "devel"

  val databasePropertiesFile = settingKey[File]("The file we use to grab the database login configuration.")

  databasePropertiesFile := {
    sourceDirectory <<=(File("hibernate/devel.properties"
  }

  val hibernateDDLOutputFile = settingKey[File]("Where we save DDL files.")

  hibernateDDLOutputFile := {
    target.value / "sql" / "stuff.sql"
  }

  val hibernateConfig = settingKey[File]("The hibernate.cfg.xml file")

  hibernateConfig := {
    sourceDirectory.value / "hibernate" / "resources" / "hibernate.cfg.xml"
  }

  val makeHibernateDDL = taskKey[Seq[File]]("Creates DDL files from hibernate classes.")

  makeHibernateDDL := {
    val output = hibernateDDLOutputFile.value
    IO.createDirectory(output.getParentFile)
    // RUN compile
    val projectClasspath = (fullClasspath in Runtime).value
    val dbProperties = new java.util.Properties
    IO.load(dbProperties, databasePropertiesFile.value)
    val hbmConfig = hibernateConfig.value
    import org.hibernate.cfg.Configuration
    val cfg = new Configuration()
    for (fileData <- projectClasspath) {
      if (fileData.data.getName endsWith ".jar") cfg.addJar(fileData.data)
      else cfg.addDirectory(fileData.data)
    }
    cfg.setProperties(dbProperties)
    import org.hibernate.tool.hbm2ddl.SchemaExport
    val schemaExport = new SchemaExport(cfg)
      .setHaltOnError(false)
      .setOutputFile(output.getCanonicalPath)
      .setDelimiter(";\n")
    schemaExport.execute(false, true, false, true)
    Seq(output)
  }

}