(ns gravie-developer-test.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[gravie-developer-test started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[gravie-developer-test has shut down successfully]=-"))
   :middleware identity})
