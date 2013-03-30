(ns lake.populate
  (:require [charts.utils :refer [to-i]]
            [metals.data :as data]))

(defn -main [& args]
  (data/populate (or (to-i (first args)) 1)))
