(ns migrations.001-base-schema
  (:require [korma.db              :refer :all]
            [korma.core            :refer :all]
            [config.utils          :refer :all]
            [config.migrate-config :refer :all]))

(defn up []
  (exec-raw "CREATE TABLE IF NOT EXISTS metals(
             date DATE,
             g_dollar INT, g_rub INT,
             s_dollar INT, s_rub INT,
             p_dollar INT, p_rub INT,
             created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now());"))

(defn down []
  (exec-raw "DROP TABLE IF EXISTS metals;"))

(defn -main []
  (defdb db-for-migration (postgres (database-config)))
  (up))
