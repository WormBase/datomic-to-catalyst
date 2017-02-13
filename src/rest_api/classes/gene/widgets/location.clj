(ns rest-api.classes.gene.widgets.location
  (:require
    [clojure.string :as str]
    [datomic.api :as d]
    [pseudoace.utils :as pace-utils]
    [rest-api.classes.gene.sequence :as sequence-fns]
    [rest-api.classes.gene.generic :as generic]
    [rest-api.formatters.object :as obj :refer  [pack-obj]]))

(defn genetic-position [gene]
  (let [segment (sequence-fns/get-longest-segment gene)
        gene-map (:gene/map gene)
        chr (if (nil? gene-map) nil (:map/id (:gene.map/map gene-map)))
        map-position (if (nil? gene-map) nil (:map-position/position gene-map))
        error (if (nil? map-position) nil (:map-error/error map-position))
        position (if (nil? map-position) nil (:map-position.position/float map-position))]
    {:data [{:chromosome chr
            :position position
            :error error
            :formatted (format "%s:%2.2f +/- %2.3f cM" chr position (if (nil? error) 0 error))
            :method ""}]
     :description (str "Genetic position of Gene:" (:gene/id gene))}))

(defn tracks [gene]
  {:data (if (:gene/corresponding-transposon gene)
           ["TRANSPOSONS"
            "TRANSPOSON_GENES"]
           ["GENES"
            "VARIATIONS_CLASSICAL_ALLELES"
            "CLONES"])
   :description "tracks displayed in GBrowse"})

(defn- genomic-obj [gene]
 (if-let [segment (sequence-fns/get-longest-segment gene)]
           (let [[start, stop] (->> segment
                                     ((juxt :start :end))
                                     (sort-by +))]
             (sequence-fns/create-genomic-location-obj start stop gene segment nil true))))

(defn genomic-position [gene]
  {:data [(genomic-obj gene)]
   :description "The genomic location of the sequence"})

(defn genomic-image [gene]
  {:data (genomic-obj gene)
   :description "The genomic location of the sequence to be displayed by GBrowse"})

(def widget
    {:name generic/name-field
     :genetic_position genetic-position
     :tracks tracks
     :genomic_position genomic-position
     :genomic_image genomic-image})