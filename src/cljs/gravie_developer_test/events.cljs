(ns gravie-developer-test.events
  (:require
    [ajax.core :as ajax]
    [re-frame.core :as rf]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

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
    {:db         (assoc db :submit-status :submitted)
     :http-xhrio {:method          :get
                  :uri             (str "/api/search-games?search-query=" (:query db))
                  :timeout         5000
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [:change-search-results]}}))
                  ;:on-failure      [:change-search-results]}}))

(rf/reg-event-db
  :change-search-results
  (fn [db [_ search-results]]
    (assoc db :search-results (:games search-results))))

;;subscriptions

(rf/reg-sub
  :search-results
  (fn [db _]
    (-> db :search-results)))

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
