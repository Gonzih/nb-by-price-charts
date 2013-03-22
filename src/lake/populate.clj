(ns lake.populate
  (:require [metals.data :as data]))

(defn -main [& args] (if (not (empty? args))
                       (data/populate (Integer/parseInt (first args)))
                       (data/populate 1)))
