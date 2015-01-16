package com.pyaanalytics

import scalaj.http.{Http, HttpOptions}
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter

object Pmdl extends App {
  val eutils_url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
  val params = "tool=medic&db=pubmed&retmode=xml&rettype=medline&retmax=5000"
  val outfile = new File("../pe_train_pm_xml.txt")
  val missfile = new File("../pe_train_pm_xml_missed.txt")
  val writer = new BufferedWriter(new FileWriter(outfile))
  val missed = new BufferedWriter(new FileWriter(missfile))
  val idfile = io.Source.fromFile("../pe_pmids.txt")
  val ids = idfile.getLines

  for (group <- ids grouped 5000) {
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
}
