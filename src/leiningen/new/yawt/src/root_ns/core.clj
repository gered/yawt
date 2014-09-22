(ns {{root-ns}}.core
  (:gen-class)
  (:require [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.server.standalone :refer [serve]]
            [edn-config.core :refer [env]]
            [{{root-ns}}.handler :refer [app init destroy]]))

(defonce server (atom nil))

(defn get-handler []
  (-> #'app
      (wrap-file "resources")
      (wrap-file-info)))

(defn start-server [& [port]]
  (let [port (if port (Integer/parseInt port) 3000)]
    (reset! server
            (serve (get-handler)
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