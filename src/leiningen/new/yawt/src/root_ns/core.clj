(ns {{root-ns}}.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [noir.util.middleware :refer [app-handler]]
            [taoensso.timbre :refer [log set-config!]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.server.standalone :refer [serve]]
            [ring.middleware.file :refer [wrap-file]]
{{#webapp}}
            [clj-jtwig.core :as jtwig]
            [clj-jtwig.web.middleware :refer [wrap-servlet-context-path]]
{{/webapp}}
            [edn-config.core :refer [env]]
            [prone.middleware :as prone]
{{#postgresql}}
            [{{root-ns}}.db :as db]
{{/postgresql}}
            [{{root-ns}}.routes :refer [main-public-routes api-routes]]
            [{{root-ns}}.middleware :refer [wrap-exceptions not-found-handler]]
            [{{root-ns}}.utils :refer [log-formatter]]))

(defroutes default-handler-routes
  (route/resources "/")
  (not-found-handler))

(def app
  (app-handler
    [main-public-routes api-routes default-handler-routes]
    :middleware [(if (env :dev)
                   prone/wrap-exceptions
                   wrap-exceptions){{#webapp}}
                 wrap-servlet-context-path{{/webapp}}]
    :access-rules []
    :formats [:json-kw :edn]))

(defn init []
  (set-config! [:shared-appender-config :spit-filename] "{{root-ns}}.log")
  (set-config! [:appenders :spit :enabled?] true)
  (set-config! [:fmt-output-fn] log-formatter)

  (log :info "Starting up ...")

  (if (env :repl)
    (log :info "Running in REPL."))

{{#webapp}}
  (when (env :dev)
    (log :info "Running in :dev environment.")
    (log :info "Disabling Jtwig template caching.")
    (jtwig/set-options! :cache-compiled-templates false)
    (jtwig/set-options! :check-for-minified-web-resources false))
{{/webapp}}
{{#webservice}}
  (when (env :dev)
    (log :info "Running in :dev environment."))
{{/webservice}}
{{#postgresql}}

  (try
    (db/init!)
    (log :info "Database access initialized.")
    (catch Exception ex
      (throw (Exception. "Database not available or bad connection information specified." ex))))
{{/postgresql}}

  (log :info "Application init finished."))

(defn destroy []
  (log :info "Shutting down ..."))


;; support functions for starting the web app in a REPL / running an uberjar directly
;; (not used otherwise)

(defonce server (atom nil))

(defonce server (atom nil))

(defn start-server [& [port]]
  (let [port (if port (Integer/parseInt port) 8080)]
    (reset! server
            (serve
              (-> #'app
                  (wrap-file "resources")
                  (wrap-file-info))
              {:port          port
               :init          init
               :auto-reload?  true
               :destroy       destroy
               :join?         false
               :open-browser? (not (env :dont-open-browser?))}))
    (println (str "You can view the site at http://localhost:" port))))

(defn stop-server []
  (.stop @server)
  (reset! server nil))

(defn -main [& args]
  (start-server))
