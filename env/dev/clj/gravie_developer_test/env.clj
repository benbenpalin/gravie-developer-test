(ns gravie-developer-test.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [gravie-developer-test.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[gravie-developer-test started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[gravie-developer-test has shut down successfully]=-"))
   :middleware wrap-dev})
