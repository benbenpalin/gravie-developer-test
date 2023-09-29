(ns gravie-developer-test.views.search
  (:require
    [re-frame.core :as rf]))

(defn result [{:keys [name image]}]
  (let [cart @(rf/subscribe [:cart])]
    [:div {:style {:margin-bottom :20px}}
     [:div name]
     [:img {:src image}]
     [:div (if (cart name)
             {:style {:cursor :not-allowed
                      :background-color :blue
                      :opacity 0.5
                      :width :100px
                      :color :white
                      :text-align :center
                      :border-radius :5px}}
             {:on-click #(rf/dispatch [:add-to-cart name])
              :style {:cursor :pointer
                      :background-color :blue
                      :width :100px
                      :color :white
                      :text-align :center
                      :border-radius :5px}})
      "Add to Cart"]]))

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

(defn search-page []
  [:section.section>div.container>div.content
   [:div "Find a game"]
   [:div
    [:input {:type "text" :name "game-query" :on-blur #(rf/dispatch [:change-query  (-> % .-target .-value)])}]
    [:button {:on-click #(rf/dispatch [:submit-query])} "SEARCH"]
    [results]]])
