(ns gravie-developer-test.events
  (:require
    [ajax.core :as ajax]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :initialize-db
  (fn [db [_ _]]
    (println (assoc  db :search-results []
                        :search-status :not-submitted))
    (assoc  db :search-results []
               :search-status :not-submitted
               :cart #{}
               :check-out-status :cart-empty
               :query "")))

(rf/reg-event-db
  :common/navigate
  (fn [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :common/route new-match))))

(rf/reg-fx
  :common/navigate-fx!
  (fn [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :common/navigate!
  (fn [_ [_ url-key params query]]
    {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-db
  :change-query
  (fn [db [_ query]]
    (assoc db :query query)))

(rf/reg-event-fx
  :submit-query
  (fn [{:keys [db]} [_ _]]
    {:db         (assoc db :search-status :submitted
                           :search-results [])
     :http-xhrio {:method          :get
                  :uri             (str "/api/search-games?search-query=" (:query db))
                  :timeout         5000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:change-search-results :success]
                  :on-failure      [:search-failure]}}))

(rf/reg-event-db
  :search-failure
  (fn [db [_ _]]
    (assoc db :search-status :failure)))

(rf/reg-event-db
  :change-search-results
  (fn [db [_ search-status search-results]]
    (assoc db :search-results (:games search-results)
              :search-status search-status)))

(rf/reg-event-db
  :reset-search-results
  (fn [db [_ search-status search-results]]
    (assoc db :search-results {}
              :search-status :not-submitted
              :query "")))

(rf/reg-event-db
  :add-to-cart
  (fn [db [_ name]]
    (-> db
      (update :cart conj name)
      (assoc :check-out-status :items-in-cart))))

(rf/reg-event-fx
  :rent-games
  (fn [{:keys [db]} [_ _]]
    {:db (assoc db :check-out-status :complete)
     :dispatch [:clear-cart]}))

(rf/reg-event-db
  :reset-check-out-status
  (fn [db [_ _]]
    (assoc db :check-out-status :cart-empty)))

(rf/reg-event-db
  :clear-cart
  (fn [db [_ _]]
    (assoc db :cart #{})))

;;subscriptions

(rf/reg-sub
  :check-out-status
  (fn [db _]
    (-> db :check-out-status)))

(rf/reg-sub
  :cart
  (fn [db _]
    (-> db :cart)))

(rf/reg-sub
  :search-results
  (fn [db _]
    (-> db :search-results)))

(rf/reg-sub
  :search-status
  (fn [db _]
    (-> db :search-status)))

(rf/reg-sub
  :common/route
  (fn [db _]
    (-> db :common/route)))

(rf/reg-sub
  :common/page-id
  :<- [:common/route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :common/page
  :<- [:common/route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
