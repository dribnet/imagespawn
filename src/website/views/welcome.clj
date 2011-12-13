(ns website.views.welcome
  (:require [website.views.common :as common]
            [noir.response])
  (:use [noir.core :only [defpage]]
        [noir.response :only [content-type]]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to website"]))

(defpage "/" []
         (common/layout
          [:div#not-found
              [:h1 "Is this the image?"]
              [:p
                [:img {:src "get-image"}]
              ]
            ]));

(import [java.awt.image BufferedImage])
(import [java.io ByteArrayOutputStream ByteArrayInputStream])
(import [javax.imageio ImageIO])

; sample function
(defn justx [x y] x)

(defn random-everywhere [x y] (rand))

; now we're thinking lispy
(defn zero-one-bound [n]
  (float (max 0.0 (min 1.0 n))))

(defn applyit [f g2d w h]
  (doseq [x (range w) y (range h)]
    (let [v (zero-one-bound (f x y))]
    (.setColor g2d (java.awt.Color. v v v))
    (.fillRect g2d x y 1 1 ))))

(defn pngdata []
  (def image (BufferedImage. 256 256 BufferedImage/TYPE_INT_RGB))
  (def g2d (.createGraphics image))
  (applyit random-everywhere g2d 256 256)
  (def os (ByteArrayOutputStream.))
  (ImageIO/write image "png" os)
  (ByteArrayInputStream. (.toByteArray os)))

(defpage "/get-image" []
  :headers {"Content-Type" "image/png",
            "Cache-Control" "public; max-age=60"}
            ; # cache image for 1 minute!}
  (pngdata))
  ;content-type "image/png" (pngdata))
  
(defpage "/test" []
  "this is a real test")
