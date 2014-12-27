(ns {{root-ns}}.utils
  (:require
    [clojure.stacktrace :refer [print-stack-trace]]))

(defn get-throwable-stack-trace [throwable]
  (if throwable
    (with-out-str
      (print-stack-trace throwable))))
