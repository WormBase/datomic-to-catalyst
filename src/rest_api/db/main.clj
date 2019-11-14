(ns rest-api.db.main
  (:require
   [datomic.api :as d]
   [environ.core :as environ]
   [mount.core :as mount]))

(defn datomic-uri []
  (environ/env :wb-db-uri))

(defn- connect []
 (let [db-uri (datomic-uri)]
  (do
   (println (format "Using Datomic: %s" db-uri))
   (d/connect db-uri))))

(defn- connect-homology []
 (let [uri (datomic-uri)]
  (do
   (let [db-uri (str uri "-homology")]
    (println (format "Using Datomic: %s" db-uri))
    (d/connect db-uri)))))

(defn- disconnect [conn]
  (d/release conn))

(mount/defstate datomic-conn
  :start (connect)
  :stop (disconnect datomic-conn))

(mount/defstate datomic-homology-conn
  :start (connect-homology)
  :stop (disconnect datomic-conn))
