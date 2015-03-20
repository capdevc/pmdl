package com.pyaanalytics

import scalaj.http.{Http, HttpOptions}
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter

import scopt.OptionParser

object Pmdl extends App {
  case class PmdlConfig(pmidFile: String = "",
                        outputFile: String = "",
                        missFile: String = "",
                        maxResults: Int = 5000)

  val parser = new OptionParser[PmdlConfig]("PMGraphX") {

    arg[String]("pmidFile") valueName("pmidFile") action {
      (x, c) => c.copy(pmidFile = x)
    }

    arg[String]("outputFile") valueName("outputFile") action {
      (x, c) => c.copy(outputFile = x)
    }

    arg[String]("missFile") valueName("missFile") action {
      (x, c) => c.copy(missFile = x)
    }

    opt[Int]('m', "maxResults") valueName("maxResults") action {
      (x, c) => c.copy(maxResults = x)
    }
  }

  parser.parse(args, PmdlConfig()) match {
    case Some(config) => {
      val eutils_url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
      val params = "tool=medic&db=pubmed&retmode=xml&rettype=medline&retmax=" + config.maxResults.toInt
      val outfile = new File(config.outputFile)
      val missfile = new File(config.missFile)
      val writer = new BufferedWriter(new FileWriter(outfile))
      val missed = new BufferedWriter(new FileWriter(missfile))
      val idfile = io.Source.fromFile(config.pmidFile)
      val ids = idfile.getLines

      for (group <- ids grouped config.maxResults) {
        try {
          val idstring = group.mkString("&id=", "&id=", "")
          val result = Http.postData(eutils_url, params + idstring)
            .option(HttpOptions.readTimeout(50000))
            .asString
          writer.write(result)
        } catch {
          case e: Exception => {
            println("Caught Exception: " + e)
            missed.write(group.mkString("", "\n", "\n"))
          }
        }
      }
    } case None => {
      System.exit(1)
    }
  }
}
