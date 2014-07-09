/**
 *
 */
package org.dbpedia.media_extractor.flickr

import java.net.URI
import java.util.Properties
import java.util.Scanner
import scala.xml.XML
import org.scalatest.FunSpec
import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.FlickrApi
import org.scribe.model.OAuthRequest
import org.scribe.model.Verb
import org.scribe.model.Verifier
import com.hp.hpl.jena.rdf.model._
import org.apache.jena.vocabulary._
import org.scribe.builder.api.FlickrApi

import org.dbpedia.media_extractor.flickr._

/**
 * @author allentiak
 *
 */
class FlickrRestApiTest extends FunSpec {

  describe("A Flickr instance") {

    it("should log in into Flickr and do stuff") {

      it("should load non-empty Flickr credentials from an external file, generate a session with those credentials, and save the token into a file") {
        //FIXME: correct method invocation
        val flickrOAuthSession = FlickrOAuthSession("/flickr.setup.properties")
        flickrOAuthSession.postCreate()
      }
      //TODO: correct method invocation
      it("should load the token")(pending)

      it("should invoke test methods") {
        it("should invoke method 'flickr.test.echo'") {
          //FIXME: correct method invocation
          val echoResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.echo")
          assert(echoResponse.getMessage() === "OK")
        }

        it("should invoke method 'flickr.test.login'") {
          //FIXME: correct method invocation
          val loginResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.login")
          assert(loginResponse.getMessage() === "OK")
        }

        it("should invoke method 'flickr.test.null'") {
          //FIXME: correct method invocation
          val nullResponse = flickrOAuthSession.invoke_parameterless_method("flickr.test.null")
          assert(nullResponse.getMessage() === "OK")
        }
      }

      //Here the flickr.photos.search test should start

      println("Building the access request to the protected resource flickr.photos.search...")
      var photosSearchRequest = new OAuthRequest(Verb.POST, endPointUri.toString())

      /*
       * Brussels:
      latitude: 50°51′0″N
      longitude: 4°21′0″E
      */

      val lat = 50.85
      val lon = 4.35

      /*
       <licenses>
  			<license id="0" name="All Rights Reserved" url="" />
  			<license id="1" name="Attribution-NonCommercial-ShareAlike License" url="http://creativecommons.org/licenses/by-nc-sa/2.0/" />
  			<license id="2" name="Attribution-NonCommercial License" url="http://creativecommons.org/licenses/by-nc/2.0/" />
  			<license id="3" name="Attribution-NonCommercial-NoDerivs License" url="http://creativecommons.org/licenses/by-nc-nd/2.0/" />
  			<license id="4" name="Attribution License" url="http://creativecommons.org/licenses/by/2.0/" />
  			<license id="5" name="Attribution-ShareAlike License" url="http://creativecommons.org/licenses/by-sa/2.0/" />
  			<license id="6" name="Attribution-NoDerivs License" url="http://creativecommons.org/licenses/by-nd/2.0/" />
  			<license id="7" name="No known copyright restrictions" url="http://flickr.com/commons/usage/" />
  			<license id="8" name="United States Government Work" url="http://www.usa.gov/copyright.shtml" />
		</licenses>
      */

      val license = "1,2"

      println("About to construct auth request for invoking method flickr.photos.search...")
      photosSearchRequest.addQuerystringParameter("method", "flickr.photos.search")
      photosSearchRequest.addQuerystringParameter("lat", lat.toString)
      photosSearchRequest.addQuerystringParameter("lon", lon.toString)
      photosSearchRequest.addQuerystringParameter("license", license)
      photosSearchRequest.addQuerystringParameter("per_page", "10")
      photosSearchRequest.addQuerystringParameter("sort", "relevance")

      println("Request about to be sent: ")
      println("QueryStringParams: " + photosSearchRequest.getQueryStringParams().toString())
      println("BodyParams: " + photosSearchRequest.getBodyParams().toString())
      println("BodyContents: " + photosSearchRequest.getBodyContents())
      println("Headers: " + photosSearchRequest.getHeaders().toString())

      myFlickrService.signRequest(accessToken, photosSearchRequest)

      println("About to invoke method flickr.photos.search...")
      val photosSearchResponse = photosSearchRequest.send()
      println("Response:")
      println("Body (this is the XML): " + photosSearchResponse.getBody())
      println("Code (200): " + photosSearchResponse.getCode())
      println("Message (OK): " + photosSearchResponse.getMessage())
      println("Headers: " + photosSearchResponse.getHeaders())
      println("Stream: " + photosSearchResponse.getStream())
      println()

      /*

This is the answer from https://secure.flickr.com/services/api/explore/flickr.photos.search
<?xml version="1.0" encoding="utf-8" ?>
<rsp stat="ok">
  <photos page="1" pages="4023" perpage="10" total="40229">
    <photo id="14153007660" owner="85268272@N05" secret="175c4b4cef" server="2905" farm="3" title="Flower after the rainfall (3/3)" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14336289671" owner="85268272@N05" secret="d7247be34c" server="2895" farm="3" title="Flower after the rainfall (2/3)" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14336289131" owner="85268272@N05" secret="c2e9f60870" server="3871" farm="4" title="Flower after the rainfall (1/3)" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14313295856" owner="85268272@N05" secret="3e62843b56" server="2898" farm="3" title="Swamps in West Brussels" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14335679414" owner="85268272@N05" secret="f346a186ed" server="2904" farm="3" title="Brussels' river" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14313296716" owner="85268272@N05" secret="b3a94caa90" server="5509" farm="6" title="Stream at the park" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14121413390" owner="85268272@N05" secret="12257a844a" server="5120" farm="6" title="Sunset in Brussels at the Basilique du Sacré-Cœur" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14313795253" owner="86559646@N00" secret="a09eda03ae" server="5592" farm="6" title="Jambinai @ AB Club" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14290930582" owner="86559646@N00" secret="56730d389f" server="3745" farm="4" title="Jambinai @ AB Club" ispublic="1" isfriend="0" isfamily="0" />
    <photo id="14288156014" owner="85268272@N05" secret="515595e554" server="5274" farm="6" title="Poppy against the wall" ispublic="1" isfriend="0" isfamily="0" />
  </photos>
</rsp>
*/

      val myXML = XML.loadString(photosSearchResponse.getBody)

      println("Printing all of 'photo' items (photo URI and page URI): ")
      (myXML \\ "rsp" \ "photos" \ "photo") foreach {
        photo =>
          println("https://farm" + (photo \ "@farm") + ".staticflickr.com/" + (photo \ "@server") + "/" + (photo \ "@id") + "_" + (photo \ "@secret") + ".jpg")
          println("https://flickr.com/photos/" + (photo \ "@owner") + "/" + (photo \ "@id"))
          println()
      }

      //Here the flickr.photos.search test should end

    }

    it("should get at least one picture")(pending)

  }
}
