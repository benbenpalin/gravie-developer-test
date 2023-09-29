(ns gravie-developer-test.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.dom :as rdom]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [gravie-developer-test.ajax :as ajax]
    [gravie-developer-test.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe])
  (:import goog.History))

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
                   [nav-link "#/" "Home" :home (when (= :complete check-out-status)
                                                 #(rf/dispatch [:reset-check-out-status]))]
                   [nav-link "#/checkout"
                    (str "Checkout " (count @(rf/subscribe [:cart])) " items")
                    :checkout
                    #(rf/dispatch [:change-search-results {} :not-submitted])]]]])))

;; Search Page

(defn result [{:keys [name image]}]
  [:div {:style {:margin-bottom :20px}}
   [:div name]
   [:img {:src image}]
   [:div {:on-click #(rf/dispatch [:add-to-cart name])
          :style {:cursor :pointer
                  :background-color :blue
                  :width :100px
                  :color :white
                  :text-align :center
                  :border-radius :5px}}
    "Add to Cart"]])

(defn results []
  (let [search-results @(rf/subscribe [:search-results])
        search-status @(rf/subscribe [:search-status])]
    (case search-status
      :success (if (not-empty search-results)
                 (into [:div] (map #(result %) search-results))
                 [:div "Sorry, there were no games for your search"])
      :submitted [:div "Searching..."]
      :failure [:div "Oh no, something went wrong! Try search again"]
      [:div])))

(defn home-page []
  [:section.section>div.container>div.content
   [:div "Find a game"]
   [:div
    [:input {:type "text" :name "game-query" :on-blur #(rf/dispatch [:change-query  (-> % .-target .-value)])}]
    [:button {:on-click #(rf/dispatch [:submit-query])} "SEARCH"]
    [results]]])

;;  Checkout Page

(defn cart-item [item item-frequencies]
  [:div
   [:span {:style {:margin-right :10px}} item]
   [:span (str (get item-frequencies item) "x")]])

(defn cart-items []
  (let [cart  @(rf/subscribe [:cart])
        item-frequencies (frequencies cart)]
    (into [:div] (map #(cart-item % item-frequencies) (set cart)))))

(defn cart []
  [:div
   [cart-items]
   [:div {:on-click #(rf/dispatch [:rent-games])
          :style {:cursor :pointer
                  :background-color :green
                  :width :250px
                  :height :50px
                  :color :white
                  :font-size :30px
                  :text-align :center
                  :border-radius :5px
                  :margin-top :10px}}
    "Complete Rental"]])

(defn payment-complete []
  [:div
   [:div "Payment Accepted"]
   [:div "Rental Complete"]])

(defn checkout-page []
  (let [check-out-status @(rf/subscribe [:check-out-status])]
    [:section.section>div.container>div.content
     [:div "CHECKOUT"]
     (case check-out-status
       :items-in-cart [cart]
       :complete [payment-complete]
       [:div "There are no items in cart"])]))

(defn page []
  (if-let [page @(rf/subscribe [:common/page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch [:common/navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page}]
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
  (mount-components))
