(ns gravie-developer-test.routes.home
  (:require
   [clojure.java.io :as io]
   [gravie-developer-test.clients.giant-bomb :as gb-client]
   [gravie-developer-test.layout :as layout]
   [gravie-developer-test.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn search-handler [request]
  (let [results (-> request
                    (get-in [:params :search-query])
                    gb-client/get-games)]
    {:status 200
     :body   {:games results}}))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/api"
    ["/search-games" {:get {:parameters {:query {:search-query string?}}
                            :handler    search-handler}}]]])
