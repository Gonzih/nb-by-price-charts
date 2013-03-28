(defproject metals "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [compojure "1.1.5"]
                 [org.clojure/data.xml "0.0.7"]
                 [hiccup "1.0.2"]
                 [korma "0.3.0-RC4"]
                 [postgresql "9.0-801.jdbc4"]
                 [clj-time "0.4.4"]
                 [cheshire "5.0.2"]]
  :plugins [[lein-ring "0.8.2"]]
  :cljsbuild {
    :builds [{
        :source-paths ["src/cljs"]
        :compiler {
          :output-to "js/main.js"
          ;:optimizations :advanced
          :optimizations :simple
          :pretty-print true
          :externs ["externs/google_vizualization_api.js"
                    "externs/jquery.js"]}}]}
  :ring {:handler metals.handler/app}
  :profiles
    {:dev        {:dependencies [[ring-mock "0.1.3"]
                                 [jayq "2.3.0"]
                                 [org.clojure/clojurescript "0.0-1586"]
                                 [log4j "1.2.15" :exclusions [javax.mail/mail
                                                              javax.jms/jms
                                                              com.sun.jdmk/jmxtools
                                                              com.sun.jmx/jmxri]]]
                  :plugins      [[lein-cljsbuild "0.3.0"]]}
     :production {:dependencies [[org.clojure/tools.logging "0.2.6"]]
                  :plugins      []}
     :migrate    {:dependencies [[drift "1.4.5"]]
                  :plugins      [[drift "1.4.5"]]}}
  :min-lein-version "2.0.0")
