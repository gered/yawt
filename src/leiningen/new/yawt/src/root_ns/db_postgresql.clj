(ns {{root-ns}}.db
  (:import (org.postgresql.ds PGPoolingDataSource))
  (:require [clojure.java.jdbc :as sql]
            [edn-config.core :refer [env]]))

(defonce db (atom nil))

(defn init!
  "should be called once when the app starts up. intializes a PGPoolingDataSource
   object so the rest of your app can execute database queries."
  []
  (reset!
    db
    (let [db-config (env :db)]
      {:datasource
        (doto (new PGPoolingDataSource)
          (.setServerName   (:host db-config))
          (.setDatabaseName (:name db-config))
          (.setPortNumber   (:port db-config))
          (.setUser         (:username db-config))
          (.setPassword     (:password db-config)))}))
  ; kind of a cheap way to verify the connection
  ; (executes a dummy query that will raise exceptions if the connection info is bad)
  (sql/query @db "select 1"))

;; TODO: add database functions here
