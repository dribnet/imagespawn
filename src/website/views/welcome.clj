(ns website.views.welcome
  (:require [website.views.common :as common]
            [noir.response])
  (:use [noir.core :only [defpage]]
        [noir.response :only [content-type]]
        [hiccup.form-helpers]
        [hiccup.page-helpers]
        [hiccup.core :only [html]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to website"]))

; (defpage "/" {:as user}
;   (common/layout
;     (form-to [:post "/generate"]
;             (user-fields user)
;             (submit-button "Wait for it..."))))

(defpage "/old" []
         (common/layout
          [:div#not-found
              [:h1 "Is this the image?"]
              [:p
                [:img {:src "get-image"}]
              ]
            ]
            ))

(import [java.awt.image BufferedImage])
(import [java.io ByteArrayOutputStream ByteArrayInputStream])
(import [javax.imageio ImageIO])

; everything below is test code only. it is used to generate
; a random test image with caching (which might or might
; not be working yet)
; it is accessed via /get-image

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
  (def pimage (BufferedImage. 256 256 BufferedImage/TYPE_INT_RGB))
  (def g2d (.createGraphics pimage))
  (applyit random-everywhere g2d 256 256)
  (def os (ByteArrayOutputStream.))
  (ImageIO/write pimage "png" os)
  (ByteArrayInputStream. (.toByteArray os)))

(defpage "/get-image" []
  {:status  200
   :headers {
      "Content-Type" "image/png",
      "Content-Disposition" "inline; filename=\"image.png\"",
      "Cache-Control" "public; max-age=60" }
   :body (pngdata)})
  
; this is my test of a 'raw' ring response, with cache-control
(defpage "/test" []
  {:status  200
   :headers {
      "Content-Type" "text/plain",
      "Content-Disposition" "inline; filename=\"test.txt\"",
      "Cache-Control" "public; max-age=60" }
   :body "Hello World from Ring"})

