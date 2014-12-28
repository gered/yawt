# YAWT: Yet Another Web Template

(a very uninspired name because I hate naming things)

YAWT is a Leiningen template to generate a base project for building:

* Clojure web applications using ClojureScript and Reagent
  * Also sets things up in a way that should work with IE8 (because -- woe is me -- I must support that crap still sometimes)
* Clojure web services exposing a JSON API (no or _very minimal_ included web UI)
* Simple support for adding initial database dependencies (PostgreSQL or CouchDB only at the moment)

This template is **primarly** written for my own use. I got tired of generating new projects with Luminus 
or other templates and then fiddling with the resulting project for 20-30 minutes afterwards to get it where I 
wanted it to be before I would work on the actual important bits of the project I was starting.

It is _not really_ intended to be completely generic "great fit for anyone" Leiningen template. There 
definitely will be some biased approaches to things like project/code organization and how things are configured. 
These things just happen to be set the way I like them to be. This may or may not work well for you, and if not, 
you might consider using another template to start off your projects instead.

However, if you do find that this template has worked out well for you -- that's great!

## Usage

```
$ lein new yawt [project-name] [options]
```

#### Options

* **webapp** (default)<br />
  Creates a new web application (ClojureScript/Reagent)
  
* **webservice**<br />
  Creates a new web service mainly intended to expose a JSON API to be used by other client apps (no ClojureScript, 
  only a barebones page served at the index out of the box)
  
* **postgres**<br />
  Includes basic PostgreSQL support (dependencies, placeholder connection configuration, etc)
  
* **couchdb**<br />
  Includes basic CouchDB support (dependencies, placeholder connection configuration, etc)
  
*Note: Currently, the "postgres" and "couchdb" options cannot be combined together.*

## Profiles

The default `project.clj` includes three Leiningen profiles, `ubejar`, `dev` and `repl`.

These profiles include a profile-specific configuration `config.edn` file from under `env-resources/` under the 
matching profile name. See [edn-config](https://github.com/yogthos/edn-config) for more
information about how this configuration file can be accessed from code.

Out of the box, the `repl` configuration will automatically run `(start-server)` when the REPL finishes loading up
and also provides a function for converting the REPL into a ClojureScript REPL (see below section for more info). The
code that accomplishes this is located under `dev/user.clj`. As per the profile's name, the `repl` profile is only
activated when you are in a Leiningen REPL.

## Development

Note that unlike some other Clojure web frameworks / templates, this template does not include lein-ring. So you
will not be able to run `lein ring server`. This is deliberately not included as it is not compatible with using
an async Jetty adapter and also obviously not usable with something like http-kit, so I don't like including it
since I prefer having my usual development tasks (commands) work the same regardless of which HTTP server I am using.

However, regardless of what Jetty adapter is being used, Ring's `wrap-reload` middleware is included in development
builds so a simple `lein run` is probably sufficient to replace what `lein ring server` gave you.

## ClojureScript

A Leiningen alias is available which is helpful during development:

```
$ lein cljsdev
```

It performs a `cljsbuild clean`, `once` and `auto`.

Generated web applications are set up with support for a ClojureScript REPL using [weasel](https://github.com/tomjakubowski/weasel).
Note that the `project.clj` uses a specific version of ClojureScript and weasel to ensure this configuration works.

To start a ClojureScript REPL, simply start up the web app in a regular REPL session and then run `cljs-repl` from
the `user` namespace (which the REPL should put you in by default). This will convert your REPL to a ClojureScript
REPL. Once `cljs-repl` returns you should refresh your browser. You can check the browser's console log to verify
that a Websocket connection was opened successfully.

Obviously ClojureScript REPL support requires a browser with Websocket support. This means IE10+ or other modern
browser.

*Currently ClojureScript tooling, and more specifically ClojureScript REPL support, can be a bit flaky at times 
unfortunately. Be warned!*

## Deploying

Simply run:

```
$ lein uberjar
```

This will clean out existing compiled output, rebuild ClojureScript using advanced optimizations and spit
out an uberjar you can deploy. This will use the config file under `env-resources/uberjar` which is usually
configured for use with "release" builds.

## License

Distributed under the the MIT License. See LICENSE for more details.