(ns {{root-ns}}.api.hello
  (:require [ring.util.http-response :refer :all]))

(defn hello []
  (ok (str "Hello from {{root-ns}}! It's " (java.util.Date.))))
