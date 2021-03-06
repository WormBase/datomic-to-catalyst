(ns rest-api.classes.antibody.widgets.overview
  (:require
   [clojure.string :as str]
   [rest-api.classes.generic-fields :as generic]
   [rest-api.formatters.object :as obj :refer [pack-obj]]))

(defn corresponding-gene [antibody]
  {:data (when-let [holders (:antibody/gene antibody)]
           (pack-obj (:antibody.gene/gene (first holders))))
   :description "the corresponding gene the antibody was generated against"})

(defn antigen [antibody]
  {:data {:comment (when-let [antigen (:antibody/antigen antibody)]
                      (:antibody.antigen/text antigen))
          :type (when-let [antigen (:antibody/antigen antibody)]
                    (str/capitalize (name (:antibody.antigen/value antigen))))}
   :description "the type and decsription of antigen this antibody was generated against"})

(defn constructed-by [antibody] ; no examples in datomic
  {:data (when-let [person (:antibody/person antibody)]
              (pack-obj (first person)))
   :description "the person who constructed the antibody"})

(defn clonality [antibody]
  {:data (when-let [clonality-kw (:antibody/clonality antibody)]
          (str/capitalize (name clonality-kw)))
   :description "the clonality of the antibody"})

(defn animal [antibody]
  {:data (when-let [animal-value (:antibody.animal/value (:antibody/animal antibody))]
            (str/capitalize (name animal-value)))
   :description "the animal the antibody was generated in"})

(def widget
  {:laboratory generic/laboratory
   :name generic/name-field
   :corresponding_gene corresponding-gene
   :antigen antigen
   :constructed_by constructed-by
   :clonality clonality
   :summary generic/summary
   :animal animal
   :remarks generic/remarks
   :historical_gene generic/historical-gene
   :other_names generic/other-names})
