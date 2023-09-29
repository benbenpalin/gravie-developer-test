(ns gravie-developer-test.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [re-frame.core :as rf]
    [gravie-developer-test.ajax :as ajax]
    [gravie-developer-test.events]
    [gravie-developer-test.views.checkout :refer [checkout-page]]
    [gravie-developer-test.views.navbar :refer [navbar]]
    [gravie-developer-test.views.search :refer [search-page]]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe])
  (:import goog.History))

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :search
           :view        #'search-page}]
     ["/checkout" {:name :checkout
                   :view #'checkout-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components []
  (rf/clear-subscription-cache!)
  (rdom/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (rf/dispatch [:initialize-db])
  (mount-components))
