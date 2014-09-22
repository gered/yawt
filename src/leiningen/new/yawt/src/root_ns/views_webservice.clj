(ns {{root-ns}}.views
  (:require [ring.util.response :as response]
            [compojure.response :refer [Renderable]])
  (:use hiccup.core
        hiccup.page))

(defn render-response [request page-fn & {:keys [params status content-type]}]
  (-> (page-fn request params)
      (response/response)
      (response/content-type (or content-type "text/html; charset=utf-8"))
      (response/status (or status 200))))

(deftype RenderablePage [page-fn params status content-type]
  Renderable
  (render [_ request]
    (render-response
      request
      page-fn
      :params params
      :status status
      :content-type content-type)))

(defn render [page-fn & {:keys [params status content-type]}]
  (RenderablePage. page-fn params status content-type))

; page render handler functions
; should all have the same function signature

(defn index-page [request params]
  (html5
    [:h2 "{{name}}"]
    [:p "TODO: replace this with some kind of service status display"]))

(defn not-found-page [request params]
  (html5
    [:h2 "Not Found"]
    [:p "The page or resource you requested could not be found."]))

(defn error-page [request params]
  (html5
    [:h2 "Server Error"]
    [:p "Your request could not be processed due to a server error."]))