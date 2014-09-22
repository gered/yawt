(ns {{root-ns}}.client.utils
  (:import goog.History)
  (:require [clojure.string :as str]
            [clojure.walk :refer [keywordize-keys]]
            [ajax.core :refer [GET]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [secretary.core :as secretary]))

(defn dev?
  "returns true if running in a dev build"
  []
  (boolean js/isDev))

(defn supports-ws?
  "returns true if the current browser supports web sockets (for cljs repl support)"
  []
  (not (-> (js/$ "html") (.hasClass "no-ws"))))

(defn old-ie?
  "returns true if the current browser is an old version of IE"
  []
  (-> (js/$ "html") (.hasClass "old-ie")))

(defn hook-browser-navigation!
  "hooks into the browser's navigation (e.g. user clicking on links, redirects, etc) such that any
   of these page navigation events are properly dispatched through secretary so appropriate routing
   can occur. should be called once on app startup"
  []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn ->api-url
  "returns a full URL to an API method suitable for using in an AJAX call. do not
   include the servlet context or any leading URL prefix (e.g. '/api'), these are
   added automatically"
  [method-url]
  (-> js/context (str "/api" method-url) (str/replace #"(/+)" "/")))

(defn api-get
  "performs an AJAX GET to an API method, passing in optional parameters. pass in
   event handler functions to hook into the results of the AJAX request.
   see '->api-url' for more information on how to pass a method url."
  [method-url & {:keys [params on-success on-error]}]
  ; HACK: manually using clojure.walk/keywordize-keys because cljs-ajax's :response-format / :keywords?
  ;       parameters don't seem to work??
  (let [url (->api-url method-url)]
    (GET url
         (merge {}
           (if params {:params params})
           (if on-success
             {:handler
               (fn [response]
                 (on-success (keywordize-keys response)))})
           (if on-error
             {:error-handler on-error})))))

; NOTE: don't try and get clever and have this function just return an atom instead
;       of needing to pass in a parameter atom.... it won't work like you expect. :)
(defn api-fetch!
  "performs an AJAX GET to an API method, passing in optional parameters. the response
   returned from the API method is set in the passed Reagent atom on success. if the
   API method results in an error, the Reagent atom is set to nil. the optional parameter
   'transform' is a function taking the raw response returned from the AJAX call which
   should return the value to set in the atom.
   see '->api-url' for more information on how to pass a method url."
  [state-atom method-url & {:keys [params transform]}]
  (let [transform (or transform identity)]
    (api-get
      method-url
      :params params
      :on-success #(reset! state-atom (transform %)))))

(defn redirect!
  "performs a redirect to another route. use in conjunction with hook-browser-navigation! for
   best results!"
  [client-app-url]
  (-> (.-location js/window)
      (set! client-app-url)))

(defn pprint-as-json [x]
  (.stringify js/JSON (clj->js x) nil "  "))