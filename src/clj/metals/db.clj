(ns metals.db
  (:require [korma.db     :refer :all]
            [korma.core   :refer :all]
            [config.utils :as config]))

(defdb db (postgres (config/database-config)))

(defn get-count []
  (-> (select :metals
        (aggregate (count :*) :cnt))
      first
      :cnt))

(def default-limit 140)

(defn get-offset [cnt lim]
  (let [off (- cnt lim)]
    (if (pos? off) off 0)))

(defn prices [lim]
  (let [cnt (get-count)]
    (if (not (zero? cnt))
      (select :metals
        (order :date :ASC)
        (offset (get-offset cnt lim))
        (limit lim)))))

(defn all-prices [] (prices default-limit))
(defn get-last [] (first (prices 1)))

(defn not-date-duplication? [date]
  (empty? (select :metals
            (where {:date date}))))

(defn not-duplicate? [args]
  (let [last* (dissoc (get-last) :created_at :date)
        args* (dissoc args :date)]
    (and (not= last* args*)
         (not-date-duplication? (:date args)))))

(defn insert-metals [args]
  (if (not-duplicate? args)
    (insert :metals
      (values args))))

(defn delete-all []
  (delete :metals))
