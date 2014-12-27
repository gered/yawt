(ns {{root-ns}}.core
  (:gen-class)
  (:require
    [compojure.core :refer [defroutes]]
    [compojure.route :as route]
    [noir.util.middleware :refer [app-handler]]
    [clojure.tools.logging :refer [info]]
    [ring-custom-jetty.server.standalone :refer [serve]]
    [ring.adapter.jetty :refer [run-jetty]]
{{#webapp}}
    [clj-pebble.core :as pebble]
    [clj-pebble.web.middleware :refer [wrap-servlet-context-path]]
{{/webapp}}
    [edn-config.core :refer [env]]
    [prone.middleware :as prone]
{{#postgresql}}
    [{{root-ns}}.db :as db]
{{/postgresql}}
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
{{#postgresql}}

  (try
    (db/init!)
    (info "Database access initialized.")
    (catch Exception ex
      (throw (Exception. "Database not available or bad connection information specified." ex))))
{{/postgresql}}

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
  (app-handler
    [main-public-routes
     api-routes
     default-handler-routes]
    :middleware [wrap-env-middleware{{#webapp}}
                 wrap-servlet-context-path{{/webapp}}]
    :access-rules []
    :formats [:json-kw :edn]))

;; support functions for starting the web app in a REPL / running an uberjar directly
;; (not used otherwise)

(defonce server (atom nil))

(defn start-server [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset!
      server
      (serve
        (get-handler)
        {:run-server-fn run-jetty
         :port          port
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
