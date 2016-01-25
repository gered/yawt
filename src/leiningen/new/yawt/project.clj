(defproject {{name}} "0.1.0-SNAPSHOT"
  :description      "FIXME: write description"
  :url              "http://example.com/FIXME"
  :min-lein-version "2.0.0"

  :main             {{root-ns}}.core

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.4.0"]
                 [metosin/ring-http-response "0.6.5"]
                 [ring-custom-jetty-server "0.1.0"]
                 [ring-server "0.4.0"]
                 [ring/ring-defaults "0.1.5" :exclusions [javax.servlet/servlet-api]]
                 [ring-middleware-format "0.7.0"]
                 [yawt-tools "0.0.1"]
{{#webapp}}
                 [org.clojure/clojurescript "1.7.145"]
                 [weasel "0.7.0" :exclusions [org.clojure/clojurescript]]
                 [clj-pebble "0.2.0"]
                 [secretary "1.2.3"]
                 [reagent "0.6.0-alpha"]
                 [cljs-ajax "0.5.3"]
                 [cljsjs/bootstrap "3.3.6-0"]
{{/webapp}}
{{#webservice}}
                 [hiccup "1.0.5"]
{{/webservice}}
{{#postgresql}}
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.postgresql/postgresql "9.4-1202-jdbc42"]
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
                 [prone "1.0.1"]]

  :plugins      [[lein-environ "1.0.0"]
{{#webapp}}
                 [lein-cljsbuild "1.1.2"]
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
  :clean-targets ^{:protect false} [:target-path
                                    [:cljsbuild :builds :main :compiler :output-dir]
                                    [:cljsbuild :builds :main :compiler :output-to]]
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
                                                             :optimizations :advanced
                                                             :pretty-print  false}}}}
{{/webapp}}
                       :ring           {:open-browser? false
                                        :stacktraces?  false
                                        :auto-reload?  false}}
             :dev     {:resource-paths ["env-resources/dev"]
                       :dependencies   [{{#webapp}}[com.cemerick/piggieback "0.2.1"]{{/webapp}}
                                        [pjstadig/humane-test-output "0.7.1"]]
{{#postgresql}}
                       :ragtime        {:migrations ragtime.sql.files/migrations
                                        :database   "jdbc:postgresql://localhost:5432/db_name?user=username&password=password"}
{{/postgresql}}
                       :injections     [(require 'pjstadig.humane-test-output)
                                        (pjstadig.humane-test-output/activate!)]}
             :repl    {:resource-paths ["env-resources/repl"]
                       :source-paths   ["dev"]}}

 :aliases {"uberjar" ["do" "clean" "uberjar"]{{#webapp}}
           "cljsdev" ["do" ["cljsbuild" "once"] ["cljsbuild" "auto"]]{{/webapp}}})