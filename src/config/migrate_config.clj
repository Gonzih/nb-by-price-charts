(ns config.migrate-config
  (:require [korma.db     :refer :all]
            [korma.core   :refer :all]
            [config.utils :refer :all]))

(defdb db (postgres (database-config)))

(defn- maybe-create-schema-table
  "Creates the schema table if it doesn't already exist."
  [& args]
  (exec-raw "CREATE TABLE IF NOT EXISTS schema_version (version INTEGER NOT NULL, created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now())"))

(defn current-db-version []
  (maybe-create-schema-table)
  (or (:version (first (select :schema_version (fields :version) (order :created_at :DESC) (limit 1)))) 0))

(defn update-db-version [version]
  (insert :schema_version (values {:version version})))

(defn migrate-config []
  {:directory "/src/migrations/"
   :namespace-prefix "migrations"
   :init maybe-create-schema-table
   :current-version current-db-version
   :update-version update-db-version})
