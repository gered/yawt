(ns {{root-ns}}.middleware
  (:require [taoensso.timbre :refer [log]]
            [noir.response :as response]
            [{{root-ns}}.views :as views])
  (:use {{root-ns}}.utils))

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
        (log :error ex "Unhandled exception.")
        (if (api-request? request)
          (->> (response/json
                 {:status  "error"
                  :message (.getMessage ex)})
               (response/status 500))
          (views/render-response
            request
            views/error-page
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
        views/not-found-page
        :status 404))))
