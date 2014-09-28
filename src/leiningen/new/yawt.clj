(ns leiningen.new.yawt
  (:require [leiningen.new.templates :refer [renderer name-to-path sanitize sanitize-ns ->files]]
            [leiningen.core.main :as main]
            [clojure.string :as str]))

(def render (renderer "yawt"))

(def supported-options
  #{"webapp"
    "webservice"
    "postgresql"
    "couchdb"})

(def default-options
  #{"webapp"})

(defn get-base-files [data]
  [[".gitignore"                  (render "gitignore" data)]
   ["project.clj"                 (render "project.clj" data)]
   ["config/dev/config.edn"       (render "config/dev/config.edn" data)]
   ["config/release/config.edn"   (render "config/release/config.edn" data)]
   ["config/repl/config.edn"      (render "config/repl/config.edn" data)]
   ["resources/log4j.properties"  (render "resources/log4j.properties" data)]
   ["src/{{path}}/core.clj"       (render "src/root_ns/core.clj" data)]
   ["src/{{path}}/handler.clj"    (render "src/root_ns/handler.clj" data)]
   ["src/{{path}}/utils.clj"      (render "src/root_ns/utils.clj" data)]
   ["src/{{path}}/api/hello.clj"  (render "src/root_ns/api/hello.clj" data)]])

(defn get-webapp-files [data]
  ["resources/public/cljs"
   "resources/public/img"
   "resources/public/js"
   ["externs/jquery.js"                                               (render "externs/jquery.js" data)]
   ["resources/public/css/screen.css"                                 (render "resources/public/css/screen.css" data)]
   ["resources/public/vendor/css/bootstrap.min.css"                   (render "resources/public/vendor/css/bootstrap.min.css" data)]
   ["resources/public/vendor/fonts/glyphicons-halflings-regular.eot"  (render "resources/public/vendor/fonts/glyphicons-halflings-regular.eot" data)]
   ["resources/public/vendor/fonts/glyphicons-halflings-regular.svg"  (render "resources/public/vendor/fonts/glyphicons-halflings-regular.svg" data)]
   ["resources/public/vendor/fonts/glyphicons-halflings-regular.ttf"  (render "resources/public/vendor/fonts/glyphicons-halflings-regular.ttf" data)]
   ["resources/public/vendor/fonts/glyphicons-halflings-regular.woff" (render "resources/public/vendor/fonts/glyphicons-halflings-regular.woff" data)]
   ["resources/public/vendor/js/bootstrap.min.js"                     (render "resources/public/vendor/js/bootstrap.min.js" data)]
   ["resources/public/vendor/js/es5-sham.min.js"                      (render "resources/public/vendor/js/es5-sham.min.js" data)]
   ["resources/public/vendor/js/es5-shim.min.js"                      (render "resources/public/vendor/js/es5-shim.min.js" data)]
   ["resources/public/vendor/js/html5shiv.min.js"                     (render "resources/public/vendor/js/html5shiv.min.js" data)]
   ["resources/public/vendor/js/jquery-1.11.1.min.js"                 (render "resources/public/vendor/js/jquery-1.11.1.min.js" data)]
   ["resources/public/vendor/js/jquery-2.1.1.min.js"                  (render "resources/public/vendor/js/jquery-2.1.1.min.js" data)]
   ["resources/public/vendor/js/react.js"                             (render "resources/public/vendor/js/react.js" data)]
   ["resources/public/vendor/js/react.min.js"                         (render "resources/public/vendor/js/react.min.js" data)]
   ["resources/views/base.html"                                       (render "resources/views/base.html" data)]
   ["resources/views/index.html"                                      (render "resources/views/index.html" data)]
   ["resources/views/error.html"                                      (render "resources/views/error.html" data)]
   ["resources/views/notfound.html"                                   (render "resources/views/notfound.html" data)]
   ["src-cljs/{{path}}/client/main.cljs"                              (render "src-cljs/root_ns/client/main.cljs" data)]
   ["src-cljs/{{path}}/client/page_components.cljs"                   (render "src-cljs/root_ns/client/page_components.cljs" data)]
   ["src-cljs/{{path}}/client/utils.cljs"                             (render "src-cljs/root_ns/client/utils.cljs" data)]
   ["src-cljs/{{path}}/client/routes/home.cljs"                       (render "src-cljs/root_ns/client/routes/home.cljs" data)]
   ["src-cljs/{{path}}/client/routes/misc.cljs"                       (render "src-cljs/root_ns/client/routes/misc.cljs" data)]
   ["src/{{path}}/middleware.clj"                                     (render "src/root_ns/middleware_webapp.clj" data)]
   ["src/{{path}}/routes.clj"                                         (render "src/root_ns/routes_webapp.clj" data)]
   ["src/{{path}}/views.clj"                                          (render "src/root_ns/views_webapp.clj" data)]
   ["dev/user.clj"                                                    (render "dev/user_webapp.clj" data)]])

(defn get-webservice-files [data]
  [["src/{{path}}/middleware.clj" (render "src/root_ns/middleware_webservice.clj" data)]
   ["src/{{path}}/routes.clj"     (render "src/root_ns/routes_webservice.clj" data)]
   ["src/{{path}}/views.clj"      (render "src/root_ns/views_webservice.clj" data)]
   ["dev/user.clj"                (render "dev/user_webservice.clj" data)]])

(defn get-postgresql-files [data]
  [["src/{{path}}/db.clj" (render "src/root_ns/db_postgresql.clj" data)]])

(defn get-couchdb-files [data]
  [["src/{{path}}/db.clj" (render "src/root_ns/db_couchdb.clj" data)]])

(defn make-project! [options data]
  (main/info "Generating a new YAWT project called" (:name data) "using options:" (str/join ", " options))
  (apply ->files data
         (concat
           (get-base-files data)
           (if (:webapp data)     (get-webapp-files data))
           (if (:webservice data) (get-webservice-files data))
           (if (:postgresql data) (get-postgresql-files data))
           (if (:couchdb data)    (get-couchdb-files data)))))

(defn invalid-options? [options]
  (or (seq (clojure.set/difference options supported-options))
      (and (some #{"webapp"} options)
           (some #{"webservice"} options))
      (and (some #{"postgresql"} options)
           (some #{"couchdb"} options))))

(defn invalid-options-message! [user-options]
  (println "Invalid options given for generating a new YAWT project.")
  (println "Valid options are:" (str/join ", " supported-options))
  (println "*** Note that you can only specify at most ONE from each of:")
  (println "    - 'webapp' and 'webservice'")
  (println "    - 'postgresql' and 'couchdb'")
  (println "*** If neither 'webapp' or 'webservice' is specified, 'webapp' is assumed")
  (println "*** Specifying no options will default to use only 'webapp'"))

(defn get-final-requested-options [requested-options]
  (let [options (if (seq requested-options)
                  (set requested-options)
                  default-options)]
    (if-not (or (some #{"webapp"} options)
                (some #{"webservice"} options))
      (conj options "webapp")
      options)))

(defn yawt
  "Creates a new YAWT (web app/service) project."
  [name & requested-options]
  (let [options (get-final-requested-options requested-options)
        data    (merge
                  {:name      name
                   :sanitized (sanitize name)
                   :root-ns   (sanitize-ns name)
                   :path      (name-to-path name)}
                  (reduce
                    #(assoc %1 (keyword %2) true)
                    {}
                    options))]
    (if (invalid-options? options)
      (invalid-options-message! options)
      (make-project! options data))))
