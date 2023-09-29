(ns gravie-developer-test.views.navbar
  (:require
    [reagent.core :as r]
    [re-frame.core :as rf]))

(defn nav-link [uri title page on-click-fn]
  [:a.navbar-item
   {:href   uri
    :class (when (= page @(rf/subscribe [:common/page-id])) :is-active)
    :on-click on-click-fn}
   title])

(defn navbar []
  (let [check-out-status @(rf/subscribe [:check-out-status])]
    (r/with-let [expanded? (r/atom false)]
                [:nav.navbar.is-info>div.container
                 [:div.navbar-brand
                  [:a.navbar-item {:href "/" :style {:font-weight :bold}} "gravie-developer-test"]
                  [:span.navbar-burger.burger
                   {:data-target :nav-menu
                    :on-click #(swap! expanded? not)
                    :class (when @expanded? :is-active)}
                   [:span][:span][:span]]]
                 [:div#nav-menu.navbar-menu
                  {:class (when @expanded? :is-active)}
                  [:div.navbar-start
                   [nav-link "#/" "Search" :search (when (= :complete check-out-status)
                                                     #(rf/dispatch [:reset-check-out-status]))]
                   [nav-link "#/checkout"
                    (str "Checkout " (count @(rf/subscribe [:cart])) " items")
                    :checkout
                    #(rf/dispatch [:reset-search-results])]]]])))
