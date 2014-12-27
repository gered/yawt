(ns user
  (:require
    weasel.repl.websocket
    cemerick.piggieback
    [edn-config.core :refer [env]])
  (:use
    {{root-ns}}.core))

(defn cljs-repl
  "after establishing a normal repl with leiningen (e.g. by running 'lein repl') *and* after
   the ring app starts up normally, run this function in the repl to convert it into a
   cljs repl. you will need to refresh the browser after running this function to connect
   your browser to the cljs repl."
  []
  (cemerick.piggieback/cljs-repl
    :repl-env (weasel.repl.websocket/repl-env
                :ip "0.0.0.0" :port 9001)))

(if (env :auto-start-server?)
  (.start (Thread. #(start-server))))
