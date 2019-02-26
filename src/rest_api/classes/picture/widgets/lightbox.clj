(ns rest-api.classes.picture.widgets.lightbox
  (:require
    [clojure.string :as str]
    [rest-api.classes.picture.core :as picture-fns]
    [rest-api.classes.expr-pattern.core :as expr-pattern]
    [rest-api.formatters.object :as obj :refer [pack-obj]]
    [rest-api.classes.generic-fields :as generic]))

(defn cropped-from [p] ;e.g.WBPicture0000007800
  {:data (some->> (:picture/cropped-from p)
                  (map pack-obj)
                  (first))
   :description "Picture that this picture was cropped from"})

(defn go-terms [p] ;e.g. WBPicture0000007800
  {:data (some->> (:picture/cellular-component p)
                  (map pack-obj)
                  (map (fn [go-obj]
                         {(:id go-obj) go-obj}))
                  (into (sorted-map)))
   :description "GO terms for this picture"})

(defn external-source [p];e.g. WBPicture0000007800
  {:data (picture-fns/external-sources p)
   :description "Information to link to the source of this picture"})

(defn contact [p] ;e.g. WBPicture0001123381
  {:data (some->> (:picture/contact p)
                  (map pack-obj)
                  (first))
   :description "Who to contact about this picture"})

(defn description [p] ;e.g. WBPicture0001123381
  {:data (when-let [description (:picture/description p)]
           (str/join  "<br />"
                     (vals
                       (reverse
                         (sort
                           (into
                             {}
                             (for [[idx s] (map-indexed (fn [idx itm] [idx itm]) description)]
                               {idx s})))))))
   :description (str "description of the Picture " (:picture/id p))})

(defn image [p]
  {:data (when-let [thumbnail  (picture-fns/pack-image p)]
           (conj (pack-obj p)
                {:thumbnail thumbnail}))
   :description "Information pertaining to the underlying image of the picture"})

(defn reference [p] ;e.g. WBPicture0001123381
  {:data (some->> (:picture/reference p)
                  (map pack-obj)
                  (first))
   :description "Paper that this picture belongs to"})

(defn anatomy-terms [p]
  {:data (some->> (:picture/anatomy p)
                  (map pack-obj)
                  (map (fn [obj]
                         {(:id obj) obj}))
                  (into (sorted-map)))
   :description "Anatomy terms for this picture"})

(defn cropped-pictures [p];e.g. WBPicture0000007799
  {:data (some->> (:picture/_cropped-from p)
                  (map pack-obj)
                  (map (fn [obj]
                         {(:id go-obj) obj}))
                  (into (sorted-map)))
   :description "Picture(s) that were cropped from this picture"})

(defn expression-patterns [p] ;e.g. WBPicture0001123381
  {:data (some->> (:picture/expr-pattern p)
                  (map (fn [ep]
                    (expr-pattern/pack ep p))))
   :description (str "expression patterns associated with the Picture: " (:picture/id p))})

(def widget
  {:name generic/name-field
   :cropped_from cropped-from
   :go_terms go-terms
   :external_source external-source
   :contact contact
   :description description
   :image image
   :reference reference
   :anatomy_terms anatomy-terms
   :cropped_pictures cropped-pictures
   :remarks generic/remarks
   :expression_patterns expression-patterns})
