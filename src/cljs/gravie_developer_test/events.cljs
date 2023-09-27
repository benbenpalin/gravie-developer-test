(ns gravie-developer-test.events
  (:require
    [re-frame.core :as rf]
    [ajax.core :as ajax]
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
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn [_ _]
    {:dispatch [:fetch-docs]}))

(rf/reg-event-db
  :change-query
  (fn [db [_ query]]
    (assoc db :query query)))

(rf/reg-event-fx
  :submit-query
  (fn [{:keys [db]} [_ _]]
    {:db (assoc db :submit-status :submitted)
     :dispatch [:change-search-results [{:game-name  "THPS"
                                         :game-image "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSq_-0XfS2Wmz9Fj-h8PTpFSgpEwaInIqrL438PQyPDdQ&s"}
                                        {:game-name  "THPS 2"
                                         :game-image "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSq_-0XfS2Wmz9Fj-h8PTpFSgpEwaInIqrL438PQyPDdQ&s"}]]}))

(rf/reg-event-db
  :change-search-results
  (fn [db [_ search-results]]
    (assoc db :search-results search-results)))

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
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))
