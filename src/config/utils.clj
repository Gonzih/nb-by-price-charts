(ns config.utils
  (:require [clojure.java.io :as io]
            [clojure.string  :as string])
  (:import [java.io PushbackReader]
           [java.net URI]))

(defn read-database-config []
  (with-open [r (io/reader "src/config/database.clj")]
    (read (PushbackReader. r))))

(defn parse-database-url []
    (let [mode   :prod
          db-uri (java.net.URI. (System/getenv "DATABASE_URL"))
          data   (string/split (.getUserInfo db-uri) #":")]
      {:db         (last (string/split (System/getenv "DATABASE_URL") #"\/"))
       :host       (.getHost db-uri)
       :port       (.getPort db-uri)
       :user       (data 0)
       :password   (data 1)
       :sslfactory (when (= mode :dev) "org.postgresql.ssl.NonValidatingFactory")
       :ssl true}))

(defn database-config []
  (if (System/getenv "DATABASE_URL")
    (parse-database-url)
    (read-database-config)))
