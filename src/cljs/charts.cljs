(ns charts
  (:use [jayq.core :only [ajax]]
        [jayq.util :only [log]])
  (:use-macros
        [jayq.macros :only [let-ajax]]))

(defn get-data [currency]
  (let-ajax [json {:url (str "/data?currency=" currency) :dataType :json}]
    (let [dt (js/google.visualization.DataTable.)]
      (doto dt
        (.addColumn "string" "Дата")
        (.addColumn "number" "Золото 1 грамм")
        (.addColumn "number" "Серебро 50 грамм")
        (.addColumn "number" "Платина 1 грамм")
        (.addRows json))
      (draw-chart dt currency))))

(def titles {"rub" "График цен продажи драгоценных металов НБ РБ (бел. руб.)"
             "dollar" "График цен продажи драгоценных металов НБ РБ (доллар США)"})

(defn options [currency]
  (clj->js {:title (titles currency)
            :width  (* (.-width  js/document) 0.95)
            :height (* (.-height js/document) 0.45)}))

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
