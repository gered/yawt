(ns {{root-ns}}.routes
  (:require
    [compojure.core :refer [defroutes context GET]]
    [{{root-ns}}.api.hello :as hello-api]
    [{{root-ns}}.views :as views]))

(defroutes
  main-public-routes
  (GET "/" [] (views/render views/index-page)))

(defroutes api-routes
  (context "/api" []
    (GET "/hello" [] (hello-api/hello))))
