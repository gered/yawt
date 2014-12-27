(ns {{root-ns}}.db
  (:require
    [com.ashafa.clutch :as couch]
    [com.ashafa.clutch.utils :as couch-utils]
    [cemerick.url :as url]
    [edn-config.core :refer [env]]))

(defn db-url [db-name]
  (let [db-config (env :db)
        url       (:url db-config)
        user      (:username db-config)
        pass      (:password db-config)
        base-url  (url/url url db-name)]
       (if (and user pass)
         (assoc base-url
           :username user
           :password pass)
         base-url)))

(def your-app-db (db-url "your-app-db"))
