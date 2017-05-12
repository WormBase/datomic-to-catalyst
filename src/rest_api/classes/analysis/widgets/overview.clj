(ns rest-api.classes.analysis.widgets.overview
  (:require
   [clojure.string :as str]
   [datomic.api :as d]
   [pseudoace.utils :as pace-utils]
   [rest-api.classes.generic :as generic]
   [rest-api.formatters.object :as obj :refer [pack-obj]]))

(defn conducted-by [analysis]
  {:data (pack-obj (first (:analysis/conducted-by analysis)))
   :description "the person that conducted the analysis"})

(defn subproject [analysis]
  {:data (pack-obj (second (:analysis/project analysis)))
   :description "the subproject of the analysis if there is one"})

(defn project [analysis]
  {:data (pack-obj (first (:analysis/project analysis)))
   :description "the project that conducted the analysis"})

(defn description [analysis]
  {:data (first (:analysis/description analysis))
   :description (str "description of the Analysis" (:analysis/id analysis))})

(defn url [analysis]
  {:data (first (:analysis/url analysis))
   :description "the url of the analysis"})

(defn based-on-wb-release [analysis]
  {:data (first (:analysis/based-on-wb-release analysis))
   :description "the WormBase release the analysis is based on"})

(defn title [analysis]
  {:data (first (:analysis/title analysis))
   :description "the title of the analysis"})

(defn based-on-db-release [analysis]
  {:data (first (:analysis/based-on-db-release analysis))
   :description "the database release the analysis is based on"})

(def widget
  {:conducted_by conducted-by
   :subproject subproject
   :project project
   :name generic/name-field
   :description description
   :url url
   :based_on_wb_release based-on-wb-release
   :title title
   :based_on_db_release based-on-db-release})
