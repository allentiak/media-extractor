package org.dbpedia.media_extractor.flickr

import scala.collection.mutable.ListBuffer
import scala.xml.Elem

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory

case class SearchResult(depictionUri: String, pageUri: String)

object FlickrWrappr2 extends App {
  val geoRDFGraph = ModelFactory.createDefaultModel()
  val dbpediaRDFGraph = ModelFactory.createDefaultModel()

  def generateUrisForFlickrSearchResponse(myXml: Elem): List[SearchResult] = {
    val resultsListBuffer = new ListBuffer[SearchResult]
    (myXml \\ "rsp" \ "photos" \ "photo") foreach {
      photo =>
        val depictionUri = "https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg"
        val pageUri = "https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id")

        resultsListBuffer += SearchResult(depictionUri, pageUri)
    }
    resultsListBuffer.toList
  }

  def addNameSpacesToRDFGraph(nsMap: Map[String, String], rdfGraph: Model) =
    nsMap.foreach { case (k, v) => rdfGraph.setNsPrefix(k, v) }

  // TODO: implement stub 
  def performFlickrGeoSearch

  // TODO: implement stub
  def performFlickrDBpediaSearch

  // TODO: implement stub
  def processFlickrGeoSearchResults

  // TODO: implement stub
  def processFlickrDBpediaSearchResults

  // TODO: implement stub
  def addGeoLocationMetadataToRDFGraph

  // TODO: implement stub
  def addGeoSearchDocumentMetadataToRDFGraph

  // TODO: implement stub
  def addDBpediaSearchDocumentMetadataToRDFGraph
}

