(ns user
  (:require
{{#sql}}
    [ragtime.jdbc :as jdbc]
    [ragtime.repl :as repl]
    [{{root-ns}}.db :as db]
{{/sql}}
    [edn-config.core :refer [env]])
  (:use
    {{root-ns}}.core))
{{#sql}}

(defn get-ragtime-config []
  {:datastore  (jdbc/sql-database db/db)
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (println "Running migrations on" (:subname db/db))
  (repl/migrate (get-ragtime-config)))

(defn rollback []
  (println "Rolling back migrations on" (:subname db/db))
  (repl/rollback (get-ragtime-config)))
{{/sql}}

(if (env :auto-start-server?)
  (.start (Thread. #(start-server))))
