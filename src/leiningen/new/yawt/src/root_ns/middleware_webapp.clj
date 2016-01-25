(ns {{root-ns}}.middleware
  (:require
    [clojure.tools.logging :refer [error]]
    [yawt-tools.response-helpers :as response]
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
            "error.html"
            :params {:stacktrace (get-throwable-stack-trace ex)}
            :status 500))))))

(defn not-found-handler []
  (fn [request]
    (if (api-request? request)
      (response/not-found
        {:status  "notfound"
         :message "The request does not match any supported API calls."})
      (views/render-response
        request
        "notfound.html"
        :status 404))))