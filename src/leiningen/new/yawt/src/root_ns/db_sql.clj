(ns {{root-ns}}.db
  (:require
    [clojure.java.jdbc :as sql]
    [edn-config.core :refer [env]]))

(def db-config (env :db))

{{#postgresql}}
(def db
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname     (str "//" (:host db-config) ":" (or (:port db-config) 5432) "/" (:name db-config))
   :user        (:username db-config)
   :password    (:password db-config)})
{{/postgresql}}
{{#mysql}}
(def db
  {:classname   "com.mysql.jdbc.Driver"
   :subprotocol "mysql"
   :subname     (str "//" (:host db-config) ":" (or (:port db-config) 3306) "/" (:name db-config))
   :user        (:username db-config)
   :password    (:password db-config)})
{{/mysql}}

(defn verify-connection
  "not really required, but can be used at app startup to verify that the database
   configuration is correct. will throw an exception if the database is unreachable."
  []
  (sql/query db "select 1"))

;; TODO: add database functions here
