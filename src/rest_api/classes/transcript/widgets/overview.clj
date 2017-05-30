(ns rest-api.classes.transcript.widgets.overview
  (:require
   [clojure.string :as str]
   [datomic.api :as d]
   [pseudoace.utils :as pace-utils]
   [rest-api.classes.generic :as generic]
   [rest-api.formatters.date :as date]
   [rest-api.formatters.object :as obj :refer [pack-obj]]))

(defn sequence-type [t]
  {:data nil ; This comes from properties section, found on another page too
   :description "the general type of the sequence"})

(defn feature [t]
  {:data nil ; can't find example
   :description "feature associated with this transcript"})

; In the perl code the structure of the data returned changes depending on if there is evidence.
; I think that the datomic db strucutre makes it so there will never be evidence.
(defn identity-field [t]
  {:data (:transcript/brief-identification t)
   :description "Brief description of the WormBase transcript"})

(defn method [t]
  {:data (:method/id (:locatable/method t))
   :description "the method used to describe the Transcript"})

(defn corresponding-all [s]
  {:data nil ; this will take a lot of work. might want to use one function for all widgets
   :k (keys s)
   :d (:db/id s)
   :description "corresponding cds, transcripts, gene for this protein"})

(def widget
  {:name generic/name-field
   :laboratory generic/laboratory
   :available_from generic/available-from
   :taxonomy generic/taxonomy
   :sequnece_type sequence-type
   :description generic/description
   :feature feature
   :identity identity-field
   :remarks generic/remarks
   :method method
   :corresponding_all corresponding-all})
