(ns gravie-developer-test.app
  (:require [gravie-developer-test.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
