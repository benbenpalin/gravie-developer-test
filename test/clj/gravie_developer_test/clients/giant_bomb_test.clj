(ns gravie-developer-test.clients.giant-bomb-test
  (:require
    [clojure.test :refer :all]
    [gravie-developer-test.clients.giant-bomb :as gb-client]))

(deftest transform-game-result-test
  (is (= {:image "https://www.giantbomb.com/a/uploads/scale_avatar/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png"
          :name "DogFight"}
         (gb-client/transform-game-result  {:image {:tiny_url "https://www.giantbomb.com/a/uploads/square_mini/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :small_url "https://www.giantbomb.com/a/uploads/scale_small/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :screen_url "https://www.giantbomb.com/a/uploads/screen_medium/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :screen_large_url "https://www.giantbomb.com/a/uploads/screen_kubrick/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :image_tags "All Images",
                                                    :super_url "https://www.giantbomb.com/a/uploads/scale_large/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :medium_url "https://www.giantbomb.com/a/uploads/scale_medium/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :thumb_url "https://www.giantbomb.com/a/uploads/scale_avatar/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :original_url "https://www.giantbomb.com/a/uploads/original/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png",
                                                    :icon_url "https://www.giantbomb.com/a/uploads/square_avatar/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png"},
                                            :name "DogFight",
                                            :resource_type "game"}))))

(deftest transform-response-test
  (is (= [{:image "https://www.giantbomb.com/a/uploads/scale_avatar/0/1992/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png"
           :name "DogFight"}
          {:image "https://www.giantbomb.com/a/uploads/scale_avatar/48/486840/3224778-kfmaincapsule.jpg"
           :name "Kool Fight"}]
         (gb-client/transform-response {:body "{\"results\":[{\"image\":{\"thumb_url\":\"https:\\/\\/www.giantbomb.com\\/a\\/uploads\\/scale_avatar\\/0\\/1992\\/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png\",\"original_url\":\"https:\\/\\/www.giantbomb.com\\/a\\/uploads\\/original\\/0\\/1992\\/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png\",\"icon_url\":\"https:\\/\\/www.giantbomb.com\\/a\\/uploads\\/square_avatar\\/0\\/1992\\/3094454-screen%20shot%202019-04-17%20at%201.08.06%20pm.png\"},\"name\":\"DogFight\",\"resource_type\":\"game\"},{\"image\":{\"thumb_url\":\"https:\\/\\/www.giantbomb.com\\/a\\/uploads\\/scale_avatar\\/48\\/486840\\/3224778-kfmaincapsule.jpg\",\"original_url\":\"https:\\/\\/www.giantbomb.com\\/a\\/uploads\\/original\\/48\\/486840\\/3224778-kfmaincapsule.jpg\",\"icon_url\":\"https:\\/\\/www.giantbomb.com\\/a\\/uploads\\/square_avatar\\/48\\/486840\\/3224778-kfmaincapsule.jpg\"},\"name\":\"Kool Fight\",\"resource_type\":\"game\"}]}"}))))
