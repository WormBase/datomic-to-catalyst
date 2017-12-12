(ns rest-api.classes.variation.widgets.location
  (:require
    [rest-api.classes.sequence.core :as sequence-fns]
    [rest-api.classes.generic-fields :as generic]))

(defn tracks [variation]
  {:data (cond
           (= "Caenorhabditis elegans"
              (:species/id (:variation/species variation)))
           ["GENES"
            "VARIATIONS_CLASSICAL_ALLELES"
            "VARIATIONS_HIGH_THROUGHPUT_ALLELES"
            "VARIATIONS_POLYMORPHISMS"
            "VARIATIONS_CHANGE_OF_FUNCTION_ALLELES"
            "VARIATIONS_CHANGE_OF_FUNCTION_POLYMORPHISMS"
            "VARIATIONS_TRANSPOSON_INSERTION_SITES"
            "VARIATIONS_MILLION_MUTATION_PROJECT"]

           (= "Caenorhabditis briggsae"
              (:species/id (:variation/species variation)))
           ["GENES"
            "VARIATIONS_POLYMORPHISMS"]

           :else
           ["GENES"])
   :description "tracks displayed in GBrowse"})

(defn jbrowse-tracks [variation]
  {:data (cond
           (= "Caenorhabditis elegans"
              (:species/id (:variation/species variation)))
           "Curated_Genes%2CClassical_alleles%2CHigh-throughput%20alleles%2CPolymorphisms%2CChange-of-function%20alleles%2CChange-of-function%20polymorphisms%2CTransposon%20insert%20sites%2CMillion%20Mutation%20Project"

           (= "Caenorhabditis briggsae"
              (:species/id (:variation/species variation)))
           "Curated_Genes%2CPolymorphisms"

           :else
           "Curated_Genes")
   :description "tracks displayed in JBrowse"})

(defn genomic-image [variation]
  {:data (first (:data (generic/genomic-position variation)))
   :description "The genomic location of the sequence to be displayed by GBrowse"})

(def widget
    {:name generic/name-field
     :genetic_position generic/genetic-position
     :tracks tracks
     :jbrowse_tracks jbrowse-tracks
     :genomic_position generic/genomic-position
     :genomic_image genomic-image})
