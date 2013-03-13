(ns metals.layout
  (:require [hiccup.core :refer :all]))

(def analytics-crap
  " var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-39250639-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();")

(defn layout []
  (html [:head [:title "График цен продажи драгоценных металов НБ РБ"]
               [:script {:type "text/javascript" :src "//www.google.com/jsapi"}]
               [:script {:type "text/javascript" :src "//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"}]
               [:script {:type "text/javascript" :src "/main.js"}]
               [:script {:type "text/javascript"} analytics-crap]]
        [:body [:div#chart-metals-dollar]
               [:div#chart-metals-rub]]))

(defn not-found [] (html [:h1 "Not Found"]))

(def render (memoize layout))
