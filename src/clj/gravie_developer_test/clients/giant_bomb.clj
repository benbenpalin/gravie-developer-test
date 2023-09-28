(ns gravie-developer-test.clients.giant-bomb
  (:require
    [clj-http.client :as client]
    [clojure.data.json :as json]))

(defn transform-game-result
  [game-result]
  (-> game-result
      (update :image :thumb_url)
      (dissoc :resource_type)))

(defn transform-response
  [game-results]
  (-> game-results
      :body
      (json/read-str :key-fn keyword)
      :results
      (#(map transform-game-result %))))

(defn get-games
  "Calls the Giant Bomb search API and returns a transformed list of results"
  [search-query]
  (transform-response
    (client/get "http://www.giantbomb.com/api/search/"
                {:query-params {:api_key "0c1b7e12dcc278a48e82df64154747aa86f1cb26"
                                :query search-query
                                :format "json"
                                :resources "game"
                                :field_list "name,image"}
                 :headers {:User-agent "gravie-developer-test"}})))
