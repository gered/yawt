(ns user
  (:require
    [edn-config.core :refer [env]])
  (:use
    {{root-ns}}.core))

(if (env :auto-start-server?)
  (.start (Thread. #(start-server))))
