import java.io.File
import sbt._
import Keys._
import sbt.File
import org.hibernate.cfg.Configuration
import org.hibernate.tool.hbm2ddl.SchemaExport

object HbPlugin extends Build {

  val databaseConfig = settingKey[String]("The database environment.  We use this to determine the database properties file to grab login information from.")

  databaseConfig := "devel"

  val databasePropertiesFile = settingKey[File]("The file we use to grab the database login configuration.")

  val scope = settingKey[Scope]("scope")

  scope := ThisScope

  databasePropertiesFile := {
    new File("/home/tstevens/workspace/gcsi/gcsi-core/src/hibernate/resources/local.properties")
//    sourceDirectory.evaluate(scope.)
  }

  val hibernateDDLOutputFile = settingKey[File]("Where we save DDL files.")

  hibernateDDLOutputFile := {
    new File("/tmp")
  }

  val hibernateConfig = settingKey[File]("The hibernate.cfg.xml file")

//  hibernateConfig <<= (sourceDirectory) map { (source) =>
//    new File(source, "hibernate/resources/hibernate.cfg.xml")
//  }

  hibernateConfig := {
    new File("/home/tstevens/workspace/gcsi/gcsi-core/src/hibernate/resources/hibernate.cfg.xml")
  }

  val hbm2ddl = taskKey[File]("Creates DDL files from hibernate classes.")

  hbm2ddl <<= (databasePropertiesFile, hibernateDDLOutputFile, hibernateConfig, (fullClasspath in Runtime)) map {
    (propFile: File, outputFile: File, config: File, cp: Keys.Classpath) => {
      IO.createDirectory(outputFile.getParentFile)
      val dbProperties = new java.util.Properties
      IO.load(dbProperties, propFile)

      val cfg = new Configuration()


      for (fileData <- cp) {
        if (fileData.data.getName endsWith ".jar") cfg.addJar(fileData.data)
        else cfg.addDirectory(fileData.data)
      }
      cfg.setProperties(dbProperties)
      val schemaExport = new SchemaExport(cfg)
        .setHaltOnError(false)
        .setOutputFile(outputFile.getCanonicalPath)
        .setDelimiter(";\n")
      schemaExport.execute(false, true, false, true)

      outputFile
    }
  }

//  makeHibernateDDL := {
//
//    val output = hibernateDDLOutputFile.value
//
//    File.createDirectory(output.getParentFile)
//    // RUN compile
//    val projectClasspath = (fullClasspath in Runtime).value
//    val dbProperties = new java.util.Properties
//    IO.load(dbProperties, databasePropertiesFile.value)
//    val hbmConfig = hibernateConfig.value
//    import org.hibernate.cfg.Configuration
//    val cfg = new Configuration()
//    for (fileData <- projectClasspath) {
//      if (fileData.data.getName endsWith ".jar") cfg.addJar(fileData.data)
//      else cfg.addDirectory(fileData.data)
//    }
//    cfg.setProperties(dbProperties)
//    import org.hibernate.tool.hbm2ddl.SchemaExport
//    val schemaExport = new SchemaExport(cfg)
//      .setHaltOnError(false)
//      .setOutputFile(output.getCanonicalPath)
//      .setDelimiter(";\n")
//    schemaExport.execute(false, true, false, true)
//    Seq(output)
//  }

}