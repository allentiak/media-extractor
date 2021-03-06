(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]
            [clojure.java.io :as io]))

(deftest input-parsing-test
  (testing "Parsing a file"
    (let [raw-data "name,age\nBart,10\nLisa,8"
          tmp-filename "resources/simpson-kids.csv"
          correctly-parsed-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])]
      (spit tmp-filename raw-data)
      (is (= (parse (slurp tmp-filename)) correctly-parsed-data) "Testing the parser")
      (io/delete-file tmp-filename))))

(deftest input-mapifying-test
  (testing "Mapifying a CSV file (with header)"
    (let [raw-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])
          correctly-mapified-data '({:name "Bart", :age "10"} {:name "Lisa", :age "8"})]
      (is (= (mapify raw-data) correctly-mapified-data)))))

(deftest stored-credentials-map-test
  (testing "Retrieving a map of stored credentials from a file"
    (let [raw-data "name,age\nBart,10\nLisa,8"
          tmp-filename "resources/simpson-kids.csv"
          correctly-mapified-data '{:name "Bart", :age "10"}]
      (spit tmp-filename raw-data)
      (is (= (stored-credentials-map tmp-filename) correctly-mapified-data) "Testing credentials storing")
      (io/delete-file tmp-filename))))

#_(deftest generate-access-token-test
   (testing "Generating Flickr OAuth Access Token"
     (let [stored-access-token-csv-file  "resources/flickr-oauth-token.csv"
           stored-credentials-csv-file   "resources/flickr-keys.csv"
           parsed-access-token           (map->OAuthToken (stored-credentials-map stored-access-token-csv-file))
           generated-access-token        (generate-access-token stored-credentials-csv-file)
           #_                             (println "parsed-access-token: "parsed-access-token)
           #_                             (println "generated-access-token: " generated-access-token)]
         (is (= parsed-access-token generated-access-token)))))

(deftest invoke-flickr-test-echo-test
  (testing "Invoking 'flickr.test.echo'."
    (let [stored-flickr-keys-csv-file         "resources/flickr-keys.csv"
          sign-request?                       false
          consumer-key                        (stored-credentials-map stored-flickr-keys-csv-file)
          stored-flickr-oauth-token-csv-file  "resources/flickr-oauth-token.csv"
          access-token                        (stored-credentials-map stored-flickr-oauth-token-csv-file)
          response                            (invoke-flickr-method "flickr.test.echo" sign-request? consumer-key access-token)
          #_                                   (println "**Response**")
          #_                                   (println response)]
      (is (= (:status response) 200)))))

(deftest perform-flickr-search-test
  (testing "Invoking 'flickr.photos.search' with geographical coordinates"
    (let [stored-flickr-keys-csv-file         "resources/flickr-keys.csv"
          sign-request?                       true
          consumer-key                        (stored-credentials-map stored-flickr-keys-csv-file)
          stored-flickr-oauth-token-csv-file  "resources/flickr-oauth-token.csv"
          access-token                        (stored-credentials-map stored-flickr-oauth-token-csv-file)
          search-text                         "Brussels"
          latitude                            "50.85"
          longitude                           "4.35"
          radius                              "5"
          results-per-query                   "3" ; Max. 30, according to Flickr's TOU
          target-licenses                     "4,5,7,8"
          response                            (perform-flickr-search "flickr.photos.search" sign-request? consumer-key access-token search-text latitude longitude radius results-per-query target-licenses)
          #_                                   (println "**Response**")
          #_                                   (println response)]
      (is (= (:status response) 200)))))

(deftest search-result->page-uri-test
  (testing "Converting a list of search results to a URI list"
    (let
        [results-file "resources/flickr.photos.search--sample-response.json"
         uris-file "resources/flickr.photos.search--pageURIs-list.edn"]
      (is (= (slurp uris-file) (convert-response results-file))))))
