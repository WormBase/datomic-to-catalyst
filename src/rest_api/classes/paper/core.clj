(ns rest-api.classes.paper.core
  (:require
   [clojure.string :as str]
   [rest-api.formatters.object :as obj]))

(defn evidence [paper]
  {:class "paper"
   :id (:paper/id paper)
   :taxonomy "all"
   :label (str
            (obj/author-list paper)
            ", "
            (if (= nil (:paper/publication-date paper))
              ""
              (first (str/split (:paper/publication-date paper)
                                #"-"))))})

(defn get-authors [p]
  (when-let [hs (:paper/author p)]
    (vals
     (into
      (sorted-map)
      (into
       {}
       (for [h hs
             :let [author (:paper.author/author h)
                   person (first (:affiliation/person h))]]
         {(:ordered/index h)
          {:id (or (:person/id person)
                   (:author/id author))
           :class (cond person "person"
                        author "author")
           :label (:author/id author)
           :taxonomy "all"}}))))))
