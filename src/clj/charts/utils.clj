(ns charts.utils
  (:require [clojure.data.xml :as xml]
            [clojure.string   :as s]
            [clj-time.coerce  :as coerce]
            [clj-time.format  :as fmt]
            [clj-time.core    :as time]))

(def formatter (fmt/formatter "MM/dd/yyyy"))

(defn to-i [v] (when-not (nil? v) (Integer/parseInt v)))

(defn parse-xml-url [url]
  (-> url
      slurp
      s/trim
      (s/replace-first #"^([\W]+)<","<")
      java.io.StringReader.
      xml/parse))

(defn all-days
  ([] (all-days (time/now)))
  ([day] (cons day (lazy-seq (all-days (time/minus day (time/days 1)))))))

(defn sql-date [date]
  (->> date
       (fmt/parse formatter)
       (coerce/to-sql-date)))
