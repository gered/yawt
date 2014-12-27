(ns {{root-ns}}.middleware
  (:require
    [clojure.tools.logging :refer [error]]
    [noir.response :as response]
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
          (->> (response/json
                 {:status  "error"
                  :message (.getMessage ex)})
               (response/status 500))
          (views/render-response
            request
            "error.html"
            :params {:stacktrace (get-throwable-stack-trace ex)}
            :status 500))))))

(defn not-found-handler []
  (fn [request]
    (if (api-request? request)
      (->> (response/json
             {:status  "notfound"
              :message "The request does not match any supported API calls."})
           (response/status 404))
      (views/render-response
        request
        "notfound.html"
        :status 404))))