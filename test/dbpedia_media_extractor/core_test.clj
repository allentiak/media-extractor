(ns dbpedia-media-extractor.core-test
  (:require [clojure.test :refer :all]
            [dbpedia-media-extractor.core :refer :all]
            [clojure.java.io :as io]
            [qarth.oauth :as oauth]
            [qarth.impl.scribe]
            [clojure.data.xml :as xml]))

(deftest input-parsing-test
  (testing "Parsing a file"
    (let [raw-data "name,age\nBart,10\nLisa,8"
          tmp-filename "simpson_kids.csv"
          correctly-parsed-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])]
      (spit tmp-filename raw-data)
      (is (= (parse (slurp tmp-filename)) correctly-parsed-data) "Testing the parser")
      (io/delete-file tmp-filename))))

(deftest input-mapifying-test
  (testing "Mapifying a CSV file (with header)"
    (let [raw-data '(["name" "age"] ["Bart" "10"] ["Lisa" "8"])
          correctly-mapified-data '({:name "Bart", :age "10"} {:name "Lisa", :age "8"})]
      (is (= (mapify raw-data) correctly-mapified-data)))))

(deftest flickr-oauth-login
  (testing "Logging into Flickr"
    (let [credentials-csv-file       "resources/flickr_keys.csv"
          mapped-login-credentials  (first (mapify (parse (slurp credentials-csv-file))))
          my-api-key                (:api_key mapped-login-credentials)
          my-api-secret             (:api_secret mapped-login-credentials)
          conf                      {:type :scribe, :provider org.scribe.builder.api.FlickrApi, :api-key my-api-key, :api-secret my-api-secret}
          _                         (println "conf: " conf)
          service                   (oauth/build conf)
          _                         (println "service: " service)
          rec                       (oauth/new-record service)
          _                         (println "rec: " rec)
          _                         (println "Auth url: " (:url rec))
          _                         (print "Enter token: ")
          _                         (flush)
          token                     (clojure.string/trim (read-line))
          _                         (println "your token: " token)
          rec                       (oauth/activate service rec token)
          _                         (println "Authorized rec: " rec)
          access-token              (:access-token rec)
          _                         (println "access-token (to be stored for future access): " access-token)
          requestor                 (oauth/requestor service rec)
          _                         (println "requestor: " requestor)
          resp                      (requestor {:url "https://api.flickr.com/services/rest/"})
          _                         (println "full response (XML): " resp)
          _                         (println "response status: " (:status resp))
          flickr-test-login         (-> resp :body xml/parse-str :content first :content first)
          _                         (println "response headers: " (pr-str (:headers resp)))
          _                         (println "flickr-test-login:" flickr-test-login)
          _                         (println "mapped-login-credentials (inline): " (first(mapify(parse(slurp credentials-csv-file)))))
          _                         (println "mapped-login-credentials (pre-calculated): " mapped-login-credentials)
          _                         (println "my-api-key (inline): " (:api_key mapped-login-credentials))
          _                         (println "my-api-key (pre-calculated): " my-api-key)
          _                         (println "my-api-secret (inline): " (:api_secret mapped-login-credentials))
          _                         (println "my-api-secret (pre-calculated): " my-api-secret)])))
