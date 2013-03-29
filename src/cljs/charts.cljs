(ns charts
  (:use [jayq.core :only [ajax]]
        [jayq.util :only [log]])
  (:use-macros
        [jayq.macros :only [let-ajax]]))

(declare draw-chart)

(defn get-locale []
  (-> (aget js/navigator "language")
      (->> (re-find #"en"))
      (or "ru")
      keyword))

(def titles {:ru {:rub    "График цен продажи драгоценных металов НБ РБ (Бел. руб.)"
                  :dollar "График цен продажи драгоценных металов НБ РБ (доллар США)"
                  :main   "График цен продажи драгоценных металов НБ РБ"}
             :en {:rub    "Metal Price Chart NB RB (BY)"
                  :dollar "Metal Price Chart NB RB (USD)"
                  :main   "Metal Prices Chart NB RB"}})

(def fields {:ru {:date "Дата"
                  :au "Золото 1гр"
                  :ag "Серебро 50гр"
                  :pt "Платина 1гр"}
             :en {:date "Date"
                  :au "Gold 1g"
                  :ag "Silver 50g"
                  :pt "Platinum 1g"}})

(defn get-data [currency]
  (let-ajax [json {:url (str "/data?currency=" currency) :dataType :json}]
    (let [dt (js/google.visualization.DataTable.)
          locale (get-locale)]
      (doto dt
        (.addColumn "string" (-> fields locale :date))
        (.addColumn "number" (-> fields locale :au))
        (.addColumn "number" (-> fields locale :ag))
        (.addColumn "number" (-> fields locale :pt))
        (.addRows json))
      (aset js/document "title" (-> titles locale :main))
      (draw-chart dt currency))))

(defn get-client-w []
  (or (aget js/document "width")
      (-> js/document (aget "body") (aget "clientWidth"))))

(defn get-client-h []
  (or (aget js/document "height")
      (-> js/document (aget "body") (aget "clientHeight"))))

(defn options [currency]
  (let [locale (get-locale)
        currency (keyword currency)]
    (clj->js {:title (-> titles locale currency)
              :colors ["#d0aa28" "#8d8d8d" "#6ba4a6"]
              :width  (* (get-client-w) 0.95)
              :height (* (get-client-h) 0.45)})))

(defn get-chart [currency]
  (js/google.visualization.LineChart.
    (.getElementById js/document (str "chart-metals-" currency))))

(defn draw-chart [data currency]
  (let [options (options currency)
        chart (get-chart currency)]
    (.draw chart data options)))

(defn draw []
  (get-data "rub")
  (get-data "dollar"))

(doto js/google
  (.load "visualization" "1" (clj->js {"packages" ["corechart"]}))
  (.setOnLoadCallback draw))
