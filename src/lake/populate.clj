(ns lake.populate
  (:require [metals.data :as data]))

(defn -main [& args]
  (data/populate (or (data/to-i (first args)) 1)))
