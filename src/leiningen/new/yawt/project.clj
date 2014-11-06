(defproject {{name}} "0.1.0-SNAPSHOT"
  :description      "FIXME: write description"
  :url              "http://example.com/FIXME"
  :min-lein-version "2.0.0"

  :source-paths     ["src"{{#webapp}} "src-cljs"{{/webapp}}]
  :main             {{root-ns}}.core

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [metosin/ring-http-response "0.5.1"]
                 [lib-noir "0.9.4"]
                 [ring-server "0.3.1"]
{{#webapp}}
                 [org.clojure/clojurescript "0.0-2371"]
                 [weasel "0.4.2"]
                 [clj-jtwig "0.5.1"]
                 [secretary "1.2.1"]
                 [reagent "0.4.3"]
                 [cljs-ajax "0.3.3"]
{{/webapp}}
{{#webservice}}
                 [hiccup "1.0.5"]
{{/webservice}}
{{#postgresql}}
                 [org.clojure/java.jdbc "0.3.5"]
                 [postgresql/postgresql "8.4-702.jdbc4"]
                 [ragtime "0.3.7"]
{{/postgresql}}
{{#couchdb}}
                 [com.ashafa/clutch "0.4.0"]
                 [com.cemerick/url "0.1.1"]
{{/couchdb}}
                 [com.taoensso/timbre "3.3.1"]
                 [edn-config "0.2"]
                 [prone "0.6.0"]]

  :plugins      [[lein-ring "0.8.13"]
{{#webapp}}
                 [lein-cljsbuild "1.0.3"]
{{/webapp}}
                 [lein-environ "1.0.0"]
                 [lein-pprint "1.1.1"]]

{{#webapp}}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                 :init-ns user}
{{/webapp}}
{{#webservice}}
  :repl-options {:init-ns user}
{{/webservice}}

  :ring {:handler {{root-ns}}.handler/app
         :init    {{root-ns}}.handler/init
         :destroy {{root-ns}}.handler/destroy}

{{#webapp}}
  :cljsbuild {:builds [{:id           "dev"
                        :source-paths ["src-cljs"]
                        :compiler     {:output-to     "resources/public/cljs/main.js"
                                       :source-map    "resources/public/cljs/main.js.map"
                                       :output-dir    "resources/public/cljs/target"
                                       :optimizations :none
                                       :pretty-print  true}}
                       {:id           "release"
                        :source-paths ["src-cljs"]
                        :compiler     {:output-to        "resources/public/cljs/main.js"
                                       :optimizations    :advanced
                                       :pretty-print     false
                                       :output-wrapper   false
                                       :externs          ["externs/jquery.js"]
                                       :closure-warnings {:non-standard-jsdoc :off}}}]}

{{/webapp}}
  :profiles {:release {:resource-paths ["config/release"]
                       :aot            :all
                       :ring           {:open-browser? false
                                        :stacktraces?  false
                                        :auto-reload?  false}}
             :dev     {:resource-paths ["config/dev"]
                       :dependencies   [[ring-mock "0.1.5"]
                                        [ring/ring-devel "1.3.1"]
{{#webapp}}
                                        [com.cemerick/piggieback "0.1.3"]
{{/webapp}}
                                        [pjstadig/humane-test-output "0.6.0"]]
                       :injections     [(require 'pjstadig.humane-test-output)
                                        (pjstadig.humane-test-output/activate!)]}
             :repl    {:resource-paths ["config/repl"]
                       :source-paths   ["dev"]}})