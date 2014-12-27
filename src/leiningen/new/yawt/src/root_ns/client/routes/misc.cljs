(ns {{root-ns}}.client.routes.misc)

(defn notfound-page []
  [:div
   [:div.page-header [:h2 "Not Found"]]
   [:p.lead "The page or resource you requested could not be found."]])