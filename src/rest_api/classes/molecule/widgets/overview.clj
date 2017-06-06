(ns rest-api.classes.molecule.widgets.overview
  (:require
   [clojure.string :as str]
   [rest-api.classes.generic-fields :as generic]
   [rest-api.formatters.object :as obj :refer [pack-obj]]))

(defn detection-status [m]
  {:data (when-let [mshs (:molecule/status m)]
           (let [msh (first mshs)]
             {:text (when-let [v (:molecule.status/value msh)]
                      (str/capitalize (name v)))
              :evidence (obj/get-evidence msh)}))
   :description "Signifies if the molecule is predicted to be present in the animal or was shown to be present in the organism through a direct detection method"})

(defn extraction-method [m]
  {:data (when-let [ems (:molecule/extraction-method m)]
           (for [em ems]
             {:text (:molecule.extraction-method/text em)
              :evidence (obj/get-evidence em)}))
   :description "Method used to extract the molecule during detection"})

(defn chembi-id [m]
  {:data (when-let [ds (:molecule/database m)]
           (first
             (not-empty
               (remove
                 nil?
                 (for [d ds
                       :let [database-name (:database/id
                                             (:molecule.database/database d))]]
                   (if (= database-name "ChEBI")
                     (:molecule.database/text d)))))))
   :description "ChEBI id of the molecule"})

(defn detection-method [m]
  {:data (when-let [dms (:molecule/detection-method m)]
           (for [dm dms]
             {:text (:molecule.detection-method/text dm) ; this is not the same as that produced by ace see :WBMol:00006459
              :evidence (obj/get-evidence dm)}))
   :description "Experimental tool used to detect molecule"})

(defn monoisotopic-mass [m]
  {:data (when-let [mm (:molecule/monoisotopic-mass m)]
           (format "%.3f" (first mm)))
   :description "Monoisotopic mass calculated from the chemical formula of the molecule"})

(defn formula [m]
  {:data (first (:molecule/formula m))
   :description "Molecular formula from ChEBI"})

(defn synonyms [m]
  {:data (:molecule/synonym m)
   :description "Other common names for the molecule"})

(defn iupac [m]
  {:data (first (:molecule/iupac m))
   :description "IUPAC name"})

(defn inchi-key [m]
  {:data (first (:molecule/inchikey m))
   :description "InChi structure key"})

(defn nonspecies-source [m]
  {:data (first (:molecule/nonspecies-source m))
   :description "Source of molecule when not generated by the organism being studied"})

(defn molecule-use [m]
  {:data (when-let [mus (:molecule/use m)]
           (for [mu mus]
             {:text (:molecule.use/text mu)
              :evidence (obj/get-evidence mu)}))
   :description "Reported uses/affects of the molecule with regards to nematode species biology"})


; Biofunction_role', 'Status', 'Detection_method', 'Extraction_method'
(defn biological-role [m] ; This is not complete. I can not find the paper evidence.
  {:data (not-empty
           (let [biofunction (for [br (:molecule/biofunction-role m)]
                               {:val (str/capitalize
                                       (name
                                         (:molecule.biofunction-role/value br)))
                                :keys (keys br)
                                :text (:molecule.biofunction-role/text br)
                                :dbid (:db/id br)
                                })]
          {:bf biofunction})
           )
   :id (:db/id m)
   :desciption "Controlled vocabulary for specific role of molecule in nematode biology, with particular regards to biological pathways"})

(defn smiles [m]
  {:data (first (:molecule/smiles m))
   :description "SMILES structure"})

(defn inchi [m]
  {:data (first (:molecule/inchi m))
   :description "InChi structure"})

(defn biofunction-role [m]
  {:data (when-let [brs (:molecule/biofunction-role m)]
           (for [br brs]
             {:text (str/capitalize
                      (name
                        (:molecule.biofunction-role/value br)))
              ; This doesn't seem to have evidence even through evidence is returned from ace. WBMol:00006417
              :evidence (obj/get-evidence br)}))
   :description "Controlled vocabulary for specific role of molecule in nematode biology, with particular regards to biological pathways"})

(def widget
  {:name generic/name-field
   :detection_status detection-status
   :extraction_method extraction-method
   :chembi_id chembi-id
   :detection_method detection-method
   :monoisotopic_mass monoisotopic-mass
   :formula formula
   :synonyms synonyms
   :remarks generic/remarks
   :iupac iupac
   :inchi_key inchi-key
   :nonspecies-source nonspecies-source
   :molecule_use molecule-use
   :biological_role biological-role
   :smiles smiles
   :inchi inchi
   :biofunction_role biofunction-role})
