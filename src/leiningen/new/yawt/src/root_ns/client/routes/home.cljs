(ns {{root-ns}}.client.routes.home
  (:require
    [reagent.core :refer [atom]]
    [{{root-ns}}.client.utils :refer [api-fetch!]]))

(defn home-page []
  (let [hello-response (atom nil)]
    (fn []
      [:div
       [:div.page-header
        [:h2 "Hello!"]]
       [:p.lead "This page is just an example placeholder that you should replace with your own app."]
       [:p "Click the button to call an example API method and see the response."]
       [:p
        [:button.btn.btn-default
         {:on-click #(api-fetch! hello-response "/hello")}
         "Call '/hello' API"]
        [:button.btn.btn-default
         {:on-click #(reset! hello-response nil)}
         "Close response display"]]
       (if @hello-response
         [:div
          [:h4 "API response:"]
          [:pre @hello-response]])])))