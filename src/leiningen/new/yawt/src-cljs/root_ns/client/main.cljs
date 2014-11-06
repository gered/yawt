(ns {{root-ns}}.client.main
  (:require [weasel.repl :as ws-repl]
            [reagent.core :as reagent]
            [secretary.core :as secretary :refer-macros [defroute]]
            [{{root-ns}}.client.page-components :refer [app-page]]
            [{{root-ns}}.client.utils :refer [hook-browser-navigation! dev? supports-ws?]]
            [{{root-ns}}.client.routes.home :refer [home-page]]
            [{{root-ns}}.client.routes.misc :refer [notfound-page]]))

(defn page [page-component & args]
  (reagent/render-component
    [app-page
     (if args
       (apply conj [page-component] args)
       [page-component])]
    (.getElementById js/document "app")))

(defroute "/" [] (page home-page))
;; TODO: other app routes here. the 'not found' route should always be last!

(defroute "*" [] (page notfound-page))

(defn init-app []
  (when (dev?)
    (enable-console-print!)
    (if (and (supports-ws?)
             (not (ws-repl/alive?)))
      (ws-repl/connect "ws://localhost:9001" :verbose true)))
  (secretary/set-config! :prefix "#")
  (hook-browser-navigation!))

(init-app)