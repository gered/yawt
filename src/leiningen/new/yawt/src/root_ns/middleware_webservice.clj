(ns {{root-ns}}.middleware
  (:require
    [clojure.tools.logging :refer [error]]
    [webtools.response-helpers :as response]
    [{{root-ns}}.views :as views])
  (:use
    {{root-ns}}.utils))

(defn- api-request? [request]
  (.startsWith
    (:uri request)
    (-> (:context request)
        (str "/api")
        (.replace "//" "/"))))

(defn wrap-exceptions [handler]
  (fn [request]
    (try
      (handler request)
      (catch Throwable ex
        (error ex "Unhandled exception.")
        (if (api-request? request)
          (response/error
            {:status  "error"
             :message (.getMessage ex)})
          (views/render-response
            request
            views/error-page
            :params {:stacktrace (get-throwable-stack-trace ex)}
            :status 500))))))

(defn not-found-handler []
  (fn [request]
    (if (api-request? request)
      (error/not-found
        {:status  "notfound"
         :message "The request does not match any supported API calls."})
      (views/render-response
        request
        views/not-found-page
        :status 404))))
