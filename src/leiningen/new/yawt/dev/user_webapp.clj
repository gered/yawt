(ns user
  (:require
    weasel.repl.websocket
    cemerick.piggieback
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

(defn cljs-repl
  "after establishing a normal repl with leiningen (e.g. by running 'lein repl') *and* after
   the ring app starts up normally, run this function in the repl to convert it into a
   cljs repl. you will need to refresh the browser after running this function to connect
   your browser to the cljs repl."
  []
  (cemerick.piggieback/cljs-repl
    :repl-env (weasel.repl.websocket/repl-env
                :ip "0.0.0.0" :port 9001)))

(if (env :auto-start-server?)
  (.start (Thread. #(start-server))))
