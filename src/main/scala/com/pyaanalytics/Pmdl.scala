package com.pyaanalytics

import scalaj.http.{Http, HttpOptions}
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter

object Pmdl extends App {
  val eutils_url = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"
  val params = "tool=medic&db=pubmed&retmode=xml&rettype=medline&retmax=5000"
  val outfile = new File("/data/pubmed/pubmed_xml.txt")
  val writer = new BufferedWriter(new FileWriter(outfile))
  val idfile = io.Source.fromFile("/data/pubmed/pm.win.txt")
  val ids = idfile.getLines

  for (group <- ids grouped 5000) {
    val idstring = group.mkString("&id=", "&id=", "")
    val result = Http.postData(eutils_url, params + idstring)
      .option(HttpOptions.readTimeout(50000))
      .asString

    writer.write(result)
  }
}
