(ns lake.cleanup
  (:require [metals.db :as db]))

(defn -main [& args] (db/delete-all))
