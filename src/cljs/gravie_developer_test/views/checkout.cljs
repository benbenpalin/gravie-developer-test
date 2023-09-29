(ns gravie-developer-test.views.checkout
  (:require
    [re-frame.core :as rf]))

(defn cart-item [item]
  [:div item])

(defn cart-items []
  (let [cart  @(rf/subscribe [:cart])]
    (into [:div] (map #(cart-item %) cart))))

(defn clear-cart-button []
  [:div {:on-click #(do (rf/dispatch [:clear-cart])
                        (rf/dispatch [:reset-check-out-status]))
         :style {:cursor :pointer
                 :background-color :blue
                 :width :100px
                 :color :white
                 :text-align :center
                 :border-radius :5px}}
   "Clear Cart"])

(defn cart []
  [:div
   [clear-cart-button]
   [:div [cart-items]]
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
