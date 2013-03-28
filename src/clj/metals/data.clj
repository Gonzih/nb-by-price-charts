(ns metals.data
  (:require [clojure.data.xml      :as xml]
            [clojure.string        :as s]
            [metals.db             :as db]
            [clj-time.core         :as tm]
            [clj-time.coerce       :as coerce]
            [clj-time.format       :as fmt]
            [cheshire.core         :as json]
            [clojure.tools.logging :as log]))

(declare reset-json)

(defn to-i [v] (when-not (nil? v) (Integer/parseInt v)))

(defn api-url [date]
  (str "http://www.nbrb.by/Services/XmlIngots.aspx?onDate=" date))

(defn parse-xml-url [url]
  (-> url
      slurp
      s/trim
      (s/replace-first #"^([\W]+)<","<")
      java.io.StringReader.
      xml/parse))

(def formatter (fmt/formatter "MM/dd/yyyy"))

(defn all-days [day]
  (cons day (lazy-seq (all-days (tm/minus day (tm/days 1))))))

; For gold and platina use 1 gramm,
; For silver use 50 gramms
(def selectors {"0" "1" "1" "50" "2" "1"})

(defn get-metal-id [item] (get-in item [:attrs :MetalId]))
(defn get-nominal  [item] (get-in item [:attrs :Nominal]))

(defn get-tags [data]
  (for [[id nominal] selectors]
    (letfn [ (selector [item]
              (and (= id      (get-metal-id item))
                   (= nominal (get-nominal  item))))]
      (filter selector data))))

(defn get-values [data date]
  (letfn [(get-price [selector data]
            (-> data
                 (->> (filter #(= selector (:tag %))))
                 first
                 (get :content)
                 first))
          (sql-date [date]
            (->> date
                 (fmt/parse formatter)
                 (coerce/to-sql-date)))
          (metal-key [id currency]
            (keyword (str (cond
                            (= id 0) \g
                            (= id 1) \s
                            (= id 2) \p)
                          "_" currency)))
          (mapfn [item]
            (let [metal-id  (get-metal-id item)
                  item      (:content item)
                  price-dol (get-price :EntitiesDollars item)
                  price-rub (get-price :EntitiesRubles  item)]
              [(to-i metal-id) (to-i price-dol) (to-i price-rub)]))
          (reducefn [col [id dol rub]]
            (-> col
                (assoc (metal-key id "dollar") dol)
                (assoc (metal-key id "rub") rub)))]
    (reduce reducefn {:date (sql-date date)} (map mapfn data))))

(defn insert-values-for-date
  "Date format is MM/dd/yyyy"
  [date]
  (log/info (str "Inserting values to db for date " date))
  (let [data (-> date
                 api-url
                 parse-xml-url
                 :content
                 get-tags
                 flatten
                 (get-values date)
                 db/insert-metals)]
    data))

(defn populate [amount]
  (log/info (str "Populating db for " amount " of days"))
  (let [all-days (all-days (tm/now))
        days (->> all-days (take amount) reverse (map #(fmt/unparse formatter %)))]
    (doall (map insert-values-for-date days)))
  (reset-json))

(defmacro get-table [currency]
  (let [metals ["g" "s" "p"]
        keys (map #(symbol (str % "_" currency)) metals)]
    `(letfn [(mapfn# [{:keys [~'date ~@keys]}]
               [(fmt/unparse formatter (coerce/from-sql-date ~'date)) ~@keys])]
       (map mapfn# (db/all-prices)))))

(defn get-json-table [currency]
  (log/info (str "Building json table for " currency))
  (json/generate-string (cond
                          (= :rub    currency) (get-table rub)
                          (= :dollar currency) (get-table dollar))))

(def json-cache (atom {}))

(defn reset-json []
  (reset! json-cache {}))

(defn json-table [currency]
  (when (nil? (@json-cache currency))
    (swap! json-cache assoc currency (get-json-table currency)))
  (@json-cache currency))
