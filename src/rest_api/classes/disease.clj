(ns rest-api.classes.disease
  (:require
    [rest-api.classes.gene.widgets.external-links :as external-links]
    [rest-api.routing :as routing]))

(routing/defroutes
  {:entity-ns "disease"
   :widget
   {:external_links external-links/widget}})
