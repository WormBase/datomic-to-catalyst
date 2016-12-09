(ns datomic-rest-api.get-handler
  (:use  pseudoace.utils)
  (:require [datomic.api :as d :refer (db history q touch entity)]
            [clojure.walk]
            [clojure.string :as str]
            [ring.adapter.jetty :refer (run-jetty)]
            [compojure.core :refer (routes GET POST ANY context wrap-routes)]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.util.response :refer (redirect file-response)]
            [cheshire.core :as json :refer (parse-string)]
            [environ.core :refer (env)]
            [mount.core :as mount]
            [datomic-rest-api.utils.db :refer (datomic-conn)]
            [datomic-rest-api.rest.core :refer (field-adaptor widget-adaptor)]
            [datomic-rest-api.rest.gene :as gene]
            [datomic-rest-api.rest.interactions :refer (get-interactions get-interaction-details)]
            [datomic-rest-api.rest.references :refer (get-references)]
            [datomic-rest-api.rest.locatable-api :refer (feature-api)]))


(declare handle-field-get)
(declare handle-widget-get)

(defn app-routes [db]
   (routes
     (GET "/" [] "<html>
                    <h5>Widgets</h5>
                    <ul>
                       <li><a href=\"./rest/widget/\">/rest/widget/</a></li>
                       <li><a href=\"./rest/field/\">/rest/field/</a></li>
                    <ul>
                  </html>")
     (GET "/rest/field/" [] "<html>
                            <ul>
                              <li>/rest/field/gene/:id/alleles-other</li>
                              <li>/rest/field/gene/:id/polymorphism</li>
                            </ul>
                            </html>")
     (GET "/rest/widget/" [] "<html>
                    <ul>
                      <li>/rest/widget/gene/:id/external_links</li>
                      <li>/rest/widget/gene/:id/overview</li>
                      <li>/rest/widget/gene/:id/history</li>
                      <li>/rest/widget/gene/:id/mapping_data</li>
                      <li>/rest/widget/gene/:id/genetics</li>
                      <li>/rest/widget/gene/:id/phenotype</li>
                    </ul>
                  </html>")
     (GET "/rest/widget/:schema-name/:id/:widget-name" [schema-name id widget-name :as request]
          (handle-widget-get db schema-name id widget-name request))
     (GET "/rest/field/:schema-name/:id/:field-name" [schema-name id field-name :as request]
          (handle-field-get db schema-name id field-name request))))


(defn init []
  (print "Making Connection\n")
  (mount/start))

(defn app [request]
  (let [db (d/db datomic-conn)
        handler (app-routes db)]
    (handler request)))

(defn- get-port [env-key & {:keys [default]
                            :or {default nil}}]
  (let [p (env env-key)]
    (cond
      (integer? p) p
      (string? p)  (parse-int p)
       :default default)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; internal functions and helper ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- json-response [data]
  (-> data
      (json/generate-string {:pretty true})
      (ring.util.response/response)
      (ring.util.response/content-type "application/json")))

(defn- resolve-endpoint [schema-name endpoint-name whitelist]
  (if-let [fn-name (-> (str/join "/" [schema-name endpoint-name])
                       (str/replace "_" "-")
                       (whitelist))]
    (resolve (symbol (str "datomic-rest-api.rest." fn-name)))))

(def ^{:private true} whitelisted-widgets
  #{"gene/overview"
    "gene/external"
    "gene/genetics"
    "gene/phenotype"
    "gene/history"
    "gene/mapping-data"})

(def ^{:private true} whitelisted-fields
  #{"gene/alleles-other"
    "gene/polymorphisms"})

;; start of REST handler for widgets and fields

(defn- handle-field-get [db schema-name id field-name request]
  (if-let [field-fn (resolve-endpoint schema-name field-name whitelisted-fields)]
    (let [adapted-field-fn (field-adaptor field-fn)
          data (adapted-field-fn db schema-name id)]
      (-> {:name id
           :class schema-name
           :url (:uri request)}
          (assoc (keyword field-name) data)
          (json-response)))
    (-> {:message "field not exist or not available to public"}
        (json-response)
        (ring.util.response/status 404))))

(defn- handle-widget-get [db schema-name id widget-name request]
  (if-let [widget-fn (resolve-endpoint schema-name widget-name whitelisted-widgets)]
    (let [adapted-widget-fn (widget-adaptor widget-fn)
          data (adapted-widget-fn db schema-name id)]
      (-> {:name id
           :class schema-name
           :url (:uri request)
           :fields data}
          (json-response)))
    (-> {:message (format "%s widget for %s not exist or not available to public"
                          (str/capitalize widget-name)
                          (str/capitalize schema-name))}
        (json-response)
        (ring.util.response/status 404))))

;; END of REST handler for widgets and fields
