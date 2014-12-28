(defproject {{name}} "0.1.0-SNAPSHOT"
  :description      "FIXME: write description"
  :url              "http://example.com/FIXME"
  :min-lein-version "2.0.0"

  :main             {{root-ns}}.core

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [metosin/ring-http-response "0.5.2"]
                 [lib-noir "0.9.5" :exclusions [javax.servlet/servlet-api]]
                 [ring-custom-jetty-server "0.1.0"]
                 [ring-server "0.3.1"]
{{#webapp}}
                 [org.clojure/clojurescript "0.0-2511"]
                 [weasel "0.4.2"]
                 [clj-pebble "0.2.0"]
                 [secretary "1.2.1"]
                 [reagent "0.5.0-alpha"]
                 [cljs-ajax "0.3.3"]
{{/webapp}}
{{#webservice}}
                 [hiccup "1.0.5"]
{{/webservice}}
{{#postgresql}}
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.postgresql/postgresql "9.2-1003-jdbc4"]
                 [clojurewerkz/ragtime "0.4.0"]
{{/postgresql}}
{{#couchdb}}
                 [com.ashafa/clutch "0.4.0"]
                 [com.cemerick/url "0.1.1"]
{{/couchdb}}
                 [org.clojure/tools.logging "0.3.1"]
                 [log4j "1.2.16"]
                 [org.slf4j/slf4j-log4j12 "1.7.1"]
                 [edn-config "0.2"]
                 [prone "0.8.0"]]

  :plugins      [[lein-environ "1.0.0"]
{{#webapp}}
                 [lein-cljsbuild "1.0.3"]
{{/webapp}}
{{#postgresql}}
                 [clojurewerkz/ragtime.lein "0.4.0"]
{{/postgresql}}
                 [lein-pprint "1.1.1"]]

{{#webapp}}
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]
                 :init-ns user}
{{/webapp}}
{{#webservice}}
  :repl-options {:init-ns user}
{{/webservice}}

{{#webapp}}
  :cljsbuild {:builds {:main
                        {:source-paths ["src/{{path}}/client"]
                         :compiler     {:output-to     "resources/public/cljs/main.js"
                                        :source-map    "resources/public/cljs/main.js.map"
                                        :output-dir    "resources/public/cljs/target"
                                        :optimizations :none
                                        :pretty-print  true}}}}

{{/webapp}}
  :profiles {:uberjar {:resource-paths ["env-resources/uberjar"]
                       :aot            :all
                       :omit-source    true
{{#webapp}}
                       :hooks          [leiningen.cljsbuild]
                       :cljsbuild      {:jar    true
                                        :builds {:main
                                                 {:compiler ^:replace
                                                            {:output-to     "resources/public/cljs/main.js"
                                                             :externs       ["externs/jquery.js"]
                                                             :optimizations :advanced
                                                             :pretty-print  false}}}}
{{/webapp}}
                       :ring           {:open-browser? false
                                        :stacktraces?  false
                                        :auto-reload?  false}}
             :dev     {:resource-paths ["env-resources/dev"]
                       :dependencies   [{{#webapp}}[com.cemerick/piggieback "0.1.3"]{{/webapp}}
                                        [pjstadig/humane-test-output "0.6.0"]]
{{#postgresql}}
                       :ragtime        {:migrations ragtime.sql.files/migrations
                                        :database   "jdbc:postgresql://localhost:5432/db_name?user=username&password=password"}
{{/postgresql}}
                       :injections     [(require 'pjstadig.humane-test-output)
                                        (pjstadig.humane-test-output/activate!)]}
             :repl    {:resource-paths ["env-resources/repl"]
                       :source-paths   ["dev"]}}

 :aliases {"uberjar" ["do" "clean" {{#webapp}}["cljsbuild" "clean"]{{/webapp}} "uberjar"]{{#webapp}}
           "cljsdev" ["do" ["cljsbuild" "clean"] ["cljsbuild" "once"] ["cljsbuild" "auto"]]{{/webapp}}})