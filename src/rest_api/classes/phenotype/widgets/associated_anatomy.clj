(ns rest-api.classes.phenotype.widgets.associated-anatomy
  (:require
    [rest-api.classes.generic-fields :as generic]
    [rest-api.formatters.object :as obj :refer  [pack-obj]]
    [pseudoace.utils :as pace-utils]))

(defn anatomy-function [p]
  {:data (let [tags ["anatomy-function"]]
           (for [tag tags]
             (case tag
               "anatomy-function"
               (when-let [afhs (:anatomy-function.phenotype/_phenotype p)]
                 (for [afh afhs
                       :let [af (:anatomy-function/_phenotype afh)]]
                   (pace-utils/vmap
                     :k (keys af)

                    :gene
                    (when-let [gene (:anatomy-function.gene/gene
                                    (:anatomy-function/gene af))]
                      (pack-obj gene))

                    :assay
                    (when-let [ahs (:anatomy-function/assay af)]
                      (for [ah ahs]
                       {:text (:ao-code/id (:anatomy-function.assay/ao-code ah))
                        :evidence (obj/get-evidence ah)}))

                    :phenotype
                    (when-let [phs (:anatomy-function.phenotype/phenotype af)]
                      (keys phs)
                      )
                    :bp_not_inv
                    (when-let [nihs (:anatomy-function/not-involved af)]
                      (for [nih nihs]
                        {:text (when-let [at (:anatomy-function.not-involved/anatomy-term nih)]
                                 (pack-obj at))
                         :evidence (obj/get-evidence nih)}))

                    :bp_inv
                    (when-let [ihs (:anatomy-function/involved af)]
                      (for [ih ihs]
                        {:text (when-let [at (:anatomy-function.involved/anatomy-term ih)]
                                 (pack-obj at))
                         :evidence (obj/get-evidence ih)}))

                   )
                 )))))
   :description "anatomy_functions associatated with this anatomy_term"})

(def widget
  {:associated_anatomy anatomy-function
   :name generic/name-field})