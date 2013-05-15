(ns charts.handler
  (:require [compojure.core         :refer :all]
            [ring.middleware.file   :refer :all]
            [ring.middleware.params :refer :all]
            [charts.utils           :refer :all]
            [compojure.handler     :as handler]
            [compojure.route       :as route]
            [views.layout          :as layout]
            [metals.data           :as data]
            [clojure.tools.logging :as log]))

(defroutes app-routes
  (GET "/" [] (layout/render))
  (GET "/data" [currency]
    (let [currency (get #{"rub" "dollar"} currency "rub")]
      (data/json-table (keyword currency))))
  (GET "/populate" [days]
    (data/populate (or (to-i days) 1))
    "ok")
  (route/not-found (layout/not-found)))

(def app
  (-> app-routes
      handler/site
      wrap-params
      (wrap-file "js")))
