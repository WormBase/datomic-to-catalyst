(ns rest-api.classes.clone.widgets.sequences
  (:require
    [clojure.string :as str]
    [rest-api.classes.generic-fields :as generic]
    [rest-api.classes.sequence.core :as sequence-fns]
    [rest-api.formatters.object :as obj :refer [pack-obj]]))

(defn strand [c]
  {:data (when-let [seg (first (:data (generic/genomic-position c)))]
           (if (< (:start seg) (:stop seg)) "+" "-"))
   :description "strand orientation of the sequence"})

(defn end-reads [c]
  {:data (some->> (:sequence/_clone-end-seq-read c)
                  (map pack-obj))
   :description "end reads associated with this clone"})

(defn sequences [c]
  {:data (some->> (:sequence/_clone c)
                  (map pack-obj))
   :description "sequences associated with this clone"})

(defn print-sequence [c]
  {:data (some->> (:sequence/_clone c)
		  (map (fn [s]
			 (if-let [h (:sequence/dna s)]
			   {:header "Sequence"
			    :sequence (-> h
					  :sequence.dna/dna
					  :dna/sequence)
			    :length (:sequence.dna/length h)}
                           (when-let [refseqobj (sequence-fns/genomic-obj c)]
                             (when-let [refseq (sequence-fns/get-sequence refseqobj)]
                           {:header "Reference Sequence"
                            :sequence refseq
                            :length (count refseq)}))
			   )))
		  (remove nil?)
		  (not-empty))
   :description "the sequence of the clone"})

(def widget
  {:name generic/name-field
   :predicted_units generic/predicted-units
   :end_reads end-reads
   :sequences sequences
   :print_sequence print-sequence
   :strand strand})
