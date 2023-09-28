(ns gravie-developer-test.handler-test
  (:require
    [clojure.test :refer :all]
    [ring.mock.request :refer :all]
    [gravie-developer-test.handler :refer :all]
    [gravie-developer-test.middleware.formats :as formats]
    [gravie-developer-test.routes.home :as routes]
    [muuntaja.core :as m]
    [mount.core :as mount])
  (:use clj-http.fake))

(defn parse-json [body]
  (m/decode formats/instance "application/json" body))

(use-fixtures
  :once
  (fn [f]
    (mount/start #'gravie-developer-test.config/env
                 #'gravie-developer-test.handler/app-routes)
    (f)))

(deftest test-app
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response)))))

  (testing "api/search-games"
    (let [response ((app) (-> (request :get "/api/search-games")
                              (query-string {:search-query "fun"})))]
      (is (= 200 (:status response))))))

(deftest search-handler-test
  (with-fake-routes
    {{:address "http://www.giantbomb.com/api/search/" :query-params {:api_key "0c1b7e12dcc278a48e82df64154747aa86f1cb26"
                                                                     :query "fun"
                                                                     :format "json"
                                                                     :resources "game"
                                                                     :field_list "name,image"}}
     (fn [request] {:status 200 :headers {} :body "{\"results\":[{\"name\":\"fun time\",\"image\":{\"thumb_url\":\"http:\\/\\/www.image.com\"}}]}"})}
    (is (= {:status 200
            :body {:games [{:name "fun time"
                            :image "http://www.image.com"}]}}
           (routes/search-handler {:params {:search-query "fun"}})))))
