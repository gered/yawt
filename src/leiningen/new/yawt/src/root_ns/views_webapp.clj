(ns {{root-ns}}.views
  (:require [clojure.string :as str]
            [clj-jtwig.core :as jtwig]
            [clj-jtwig.web.middleware :refer [*servlet-context-path*]]
            [ring.util.response :as response]
            [compojure.response :refer [Renderable]]
            [edn-config.core :refer [env]]))

(def template-path "views/")

(defn- render-template [request template params]
  (jtwig/render-resource
    (str template-path template)
    (assoc params
      :isDev   (env :dev)
      :context (:context request))))

(defn render-response [request template & {:keys [params status content-type]}]
  (-> (render-template request template params)
      (response/response)
      (response/content-type (or content-type "text/html; charset=utf-8"))
      (response/status (or status 200))))

(deftype RenderableTemplate [template params status content-type]
  Renderable
  (render [_ request]
    (render-response
      request
      template
      :params params
      :status status
      :content-type content-type)))

(defn render [template & {:keys [params status content-type]}]
  (RenderableTemplate. template params status content-type))

