(ns {{root-ns}}.core
  (:gen-class)
  (:require
    [compojure.core :refer [defroutes routes]]
    [compojure.route :as route]
    [clojure.tools.logging :refer [info]]
    [ring.server.standalone :refer [serve]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.format :refer [wrap-restful-format]]
{{#webapp}}
    [clj-pebble.core :as pebble]
    [clj-pebble.web.middleware :refer [wrap-servlet-context-path]]
{{/webapp}}
    [edn-config.core :refer [env]]
    [prone.middleware :as prone]
{{#sql}}
    [{{root-ns}}.db :as db]
{{/sql}}
    [{{root-ns}}.routes :refer [main-public-routes api-routes]]
    [{{root-ns}}.middleware :refer [wrap-exceptions not-found-handler]]))

(defn init []
  (info "Starting up ...")

  (if (env :repl)
    (info "Running in REPL."))

{{#webapp}}
  (when (env :dev)
    (info "Running in :dev environment.")
    (pebble/set-options!
      :cache false
      :check-for-minified-web-resources false))
{{/webapp}}
{{#webservice}}
  (when (env :dev)
    (info "Running in :dev environment."))
{{/webservice}}
{{#sql}}

  (try
    (db/verify-connection)
    (info "Database connection verified.")
    (catch Exception ex
      (throw (Exception. "Database not available or bad connection information specified." ex))))
{{/sql}}

  (info "Application init finished."))

(defn destroy []
  (info "Shutting down ..."))

(defn wrap-env-middleware [handler]
  (if (env :dev)
    (-> handler (prone/wrap-exceptions))
    (-> handler (wrap-exceptions))))

(defroutes default-handler-routes
  (route/resources "/")
  (not-found-handler))

(defn get-handler []
  (-> (routes
        main-public-routes
        api-routes
        default-handler-routes)
      (wrap-env-middleware){{#webapp}}
      (wrap-servlet-context-path){{/webapp}}
      (wrap-restful-format :formats [:json-kw :edn])
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))

;; support functions for starting the web app in a REPL / running an uberjar directly
;; (not used otherwise)

(defonce server (atom nil))

(defn start-server [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset!
      server
      (serve
        (get-handler)
        {:port          port
         :init          init
         :auto-reload?  (env :dev)
         :destroy       destroy
         :join?         false
         :open-browser? (not (env :dont-open-browser?))}))
    (info (str "Serving app at http://localhost:" port "/"))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(defn -main [& args]
  (start-server))
