(ns rest-api.classes.gene-test
  (:require
   [clojure.test :refer :all]
   [rest-api.classes.gene.variation :as variation]
   [rest-api.classes.gene.widgets.genetics :as genetics]
   [rest-api.db-testing :as db-testing]))

(def get-gene (partial db-testing/entity "gene"))

;; Here we register my-test-fixture to be called once, wrapping ALL tests
;; in the namespace
(use-fixtures :once db-testing/db-lifecycle)

(defn find-variation-in [id variations]
  (first (filter #(= id (-> (:variation %) (:id)))
                 (:data variations))))

;;This is a regular test function, which is to be wrapped using
;;my-test-fixture
(deftest test-alleles-and-polymorphisms
  (let [reference-allele (#'genetics/reference-allele
                          (get-gene "WBGene00006759"))
        alleles (#'variation/alleles (get-gene "WBGene00006759"))
        alleles-other (#'variation/alleles-other
                       (get-gene "WBGene00006759"))
        polymorphisms (#'variation/polymorphisms
                       (get-gene "WBGene00006759"))
        allele1 (find-variation-in "WBVar00248722" alleles)
        allele-other1 (find-variation-in "WBVar00278273" alleles-other)
        allele-other2 (find-variation-in "WBVar01495296" alleles-other)
        allele2 (->> (get-gene "WBGene00003657")
                     (#'variation/alleles)
                     (find-variation-in "WBVar00600733"))
        polymorphism1 (find-variation-in "WBVar01858901" polymorphisms)]
    (testing "reference allele"
      (is (some #(= "e66" (:label %)) (:data reference-allele))))
    (testing "allele with phenotype"
      (testing "correct variation is returned"
        (is (= "st136" (:label (:variation allele1)))))
      (testing "phenotype count is greater than 0"
        (is (> (:phen_count allele1) 0))))

    (testing "allele without phenotype"
      (testing "correct variation is returned"
        (is (= "gk965" (:label (:variation allele-other1)))))
      (testing "phenotype count is 0"
        (is (= (:phen_count allele-other1) 0))))

    (testing "polymorphism without phenotype"
      (testing "correct variation is returned"
        (is (= "WBVar01858901" (:label (:variation polymorphism1)))))
      (testing "phenotype count is 0"
        (is (= (:phen_count polymorphism1) 0))))

    (testing "processed columns of alleles"
      (testing "correct molecular change"
        (is (= "Insertion" (:molecular_change allele1))))
      (testing "contains a correct strain"
        (is (some #(= "RW7080" (:id %)) (:strain allele1))))
      (testing "contains a correct citation"
        (is (some #(= "Mori, Moerman & Waterston, 1986"
                      (:label %))
                  (:sources allele1))))
      (testing "correct location type"
        (is (some #(= "Coding exon" %)
                  (:locations allele-other1)))))

    (testing "molecular change"
      (testing "molecular change with Nonsense mutation"
        (testing "correct molecular change"
          (is (some #(= "Nonsense" %) (:effects allele-other1))))
        (testing "correct composite change"
          (is (some #(= "Q4481Amber" %)
                    (:composite_change allele-other1)))))
      (testing "molecular change with Missense mutation"
        (testing "correct molecular change"
          (is (some #(= "Missense" %)
                    (:effects allele-other2))))
        (testing "correct composite change"
          (is (some #(= "A1774T" %)
                    (:composite_change allele-other2)))))
      (testing "effects outside of CDS"
        (is (some #(= "Promoter" %) (:effects allele2)))))))


(deftest test-strains
  (let [strains (#'genetics/strains (get-gene "WBGene00006759"))]
    (testing "strains carrying unc-22 alone"
      (is (some #(= "BC18" (:id %))
                (-> (:data strains)
                    (:carrying_gene_alone_and_cgc)))))
    (testing "strains available from cgc"
      (is (some #(= "BA836" (:id %))
                (-> (:data strains)
                    (:available_from_cgc)))))))

(deftest test-rearrangements
  (let [rearrangements (#'genetics/rearrangements
                        (get-gene "WBGene00006759"))]
    (testing "negative"
      (is (some #(= "sDf22" (:id %)) (-> (:data rearrangements)
                                         (:negative)))))
    (testing "positive"
      (is (some #(= "nDf28" (:id %)) (-> (:data rearrangements)
                                         (:positive)))))))

;;(def uri (env :trace-db))
;;(def con (datomic.api/connect uri))

;;(def base_url "localhost:8008")

;;(defn first-element [sequence default]
;;  (if (nil? sequence)
;;    default
;;    (first sequence)))
;;
;;
;;(facts "about `first-element`"
;;  (fact "it normally returns the first element"
;;    (first-element [1 2 3] :default) => 1
;;    (first-element '(1 2 3) :default) => 1))
;;
;;
;;
;;(facts "The base url is simply for testing purposes"
;;  (fact "Url test"
;;  (let [base_url "localhost:8080"
;;        url base_url]
;;      (fact "Check base url"
;;           (expect uri => "http://localhost:8130")))))
