(ns rest-api.classes.person.widgets.overview
  (:require
    [datomic.api :as d]
    [pseudoace.utils :as pace-utils]
    [rest-api.formatters.date :as date]
    [rest-api.formatters.object :as obj :refer [pack-obj]]))

(defn street-address [person]
  (let [db (d/entity-db person)
        data 
        (vals
          (into
            (sorted-map)
            (into {}
                  (->> (d/q '[:find [?street-address ...]
                              :in $ ?person
                              :where [?person :person/address ?address]
                              [?address :address/street-address ?street-address]]
                            db (:db/id person))
                       (map (fn [oid]
                              (let [street-address (d/entity db oid)]
                                {(:ordered/index street-address)
                                 (:address.street-address/text street-address)})))
                       (seq)))))]
    {:data (if (empty? data) nil data)
     :description "street address of this person"}))

(defn name-field [person]
  (let [data (pack-obj "person" person)]
    {:data (if (empty? data) nil data)
     :description (format "The name and WormBase internal ID of %s" (:person/id person))}))

(defn web-page [person]
  (let [db (d/entity-db person)
        data (->> (d/q '[:find [?web-page ...]
                         :in $ ?person
                         :where [?person :person/address ?address]
                         [?address :address/web-page ?web-page]]
                       db (:db/id person))
                  (seq))]
    {:data (if (empty? data) nil data)
     :description "web-page of this person"}))

(defn orcid [person]
  (let [db (d/entity-db person)
        orcids (->> (d/q '[:find [?orcid ...]
                           :in $ ?person
                           :where [?person :person/database ?dbent]
                           [?orc :database/id "ORCID"]
                           [?dbent :person.database/database ?orc]
                           [?dbent :person.database/accession ?orcid]]
                         db (:db/id person))
                    (seq))
        data (for [orcid orcids]
               {:class "ORCID"
                :id orcid
                :label orcid})]
    {:data (if (empty? data) nil data)
     :description "ORCID of this person"}))

(defn institution [person]
  (let [db (d/entity-db person)
        data (->> (d/q '[:find [?institution ...]
                         :in $ ?person
                         :where [?person :person/address ?address]
                         [?address :address/institution ?institution]]
                       db (:db/id person))
                  (seq))]
    {:data (if (empty? data) nil data)
     :description "institution of this person"}))

(defn email [person]
  (let [db (d/entity-db person)
        data (->> (d/q '[:find [?email ...]
                         :in $ ?person
                         :where [?person :person/address ?address]
                         [?address :address/email ?email]]
                       db (:db/id person))
                  (seq))]
    {:data (if (empty? data) nil data)
     :description "email addresses of this person"}))

(defn also-known-as [person]
  {:data (when-let [names (:person/also-known-as person)]
           (not-empty
             (remove
               nil?
               (for [n names]
                 (when (not= (:label (pack-obj person)) n)
                   n)))))
   :description "other names person is also known as."})

(defn previous-addresses [person]
  (let [db (d/entity-db person)
        data
        (->> (d/q '[:find [?old-address ...]
                    :in $ ?person
                    :where [?person :person/old-address ?old-address]]
                  db (:db/id person))
             (map (fn [oid]
                    (let [old-address (d/entity db oid)]
                      (pace-utils/vmap
                        :date-modified (date/format-date (:person.old-address/datetype old-address))
                        :email (:address/email old-address)
                        :institution (first (:address/institution old-address))
                        :country (:address/country old-address)
                        :main-phone (:address/main-phone old-address)
                        :lab-phone (:address/lab-phone old-address)
                        :office-phone (:address/office-phone old-address)
                        :other-phone (:address/other-phone old-address)
                        :fax (:address/fax old-address)
                        :web-page (:address/web-page old-address)))))
             (seq))]
    {:data (if (empty? data) nil data)
     :description
     "previous addresses of this person."}))

(defn lab-representative-for [person]
  (let [db (d/entity-db person)
        data
        (->> (d/q '[:find [?laboratory ...]
                    :in $ ?person
                    :where [?laboratory :laboratory/representative ?person]]
                  db (:db/id person))
             (map (fn [oid]
                    (let [laboratory (d/entity db oid)]
                      (pace-utils/vmap
                        :taxonomy "all"
                        :class "laboratory"
                        :label (:laboratory/id laboratory)
                        :id (:laboratory/id laboratory)))))
             (seq))]
    {:data (if (empty? data) nil data)
     :description
     "Principal Investigator/Lab representativef for"}))

(def widget
  {:name                     name-field
   :email                    email
   :orcid                    orcid
   :institution              institution
   :web_page                 web-page
   :street_address           street-address
   :aka                      also-known-as
   :lab_representative_for   lab-representative-for
   :previous_addresses       previous-addresses})
