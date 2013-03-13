(ns lake.init-populate
  (:require [metals.data :as data]))

(defn -main [] (data/populate 600))
