(ns website.views.codeform
  (:require [website.views.common :as common]
            [clojure.contrib.math :as math]
            [noir.validation :as vali]
            [noir.response :as resp])
  (:use [noir.core]
        [noir.response :only [content-type]]
        [hiccup.page-helpers]
        [hiccup.form-helpers]
        [hiccup.core :only [html]]))

(defpartial layout [& content]
  (html5
    [:head
     [:title "Forms"]]
    [:body
     content]))

(defpartial user-fields [{:keys [code]}]
  ; (vali/on-error :code error-item)
  (label "code" "Code: ")
  (text-field "code" code))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defn valid? [{:keys [code]}]
  1)
  ;(vali/rule (vali/min-length? firstname 5)
  ;           [:firstname "Your first name must have more than 5 letters."])
;  (vali/rule (vali/has-value? code)
;             [:code "You must be has code"]
;    (not (vali/errors? :code))))

(defpage "/" {:as user}
  (common/layout
    (form-to [:post "/generate"]
            (user-fields user)
            (submit-button "Wait for it..."))))

(defpage "/generate" {:as user}
  (common/layout
    (form-to [:post "/generate"]
            (user-fields user)
            (submit-button "Wait for it..."))))

; (defpage [:post "/generate"] {:as user}
;   (if (valid? user)
;     (layout
;       [:p "Big Generator!" (:code user)])
;     (render "/generate" user)))

(defpage "/namespace" []
         (common/layout
           [:p "Welcome to namespace"]))

; now we're thinking lispy
(defn zero-one-bound [n]
  (float (max 0.0 (min 1.0 n))))

(defn string-to-function [fs]
  (fn [x y]
    (declare ^:dynamic x)
    (declare ^:dynamic y)
    (declare ^:dynamic abs)
    (binding [*ns* (find-ns 'website.views.codeform)
      x x
      y y
      abs math/abs
    ] (load-string fs))))

(defn apply-from-string [fs g2d w h]
  (def fun (string-to-function fs))
  (doseq [x (range w) y (range h)]
    ; x1 = x - w/2...
    ; note, y is flipped
    (let [hw (/ w 2) hh (/ h 2)]
      (let [x1 (- x hw) y1 (- hh y)]
        (let [v (zero-one-bound (fun (/ x1 hw) (/ y1 hh)))]
        (.setColor g2d (java.awt.Color. v v v))
        (.fillRect g2d x y 1 1 ))))))

(import [java.awt.image BufferedImage])
(import [java.io ByteArrayOutputStream ByteArrayInputStream])
(import [javax.imageio ImageIO])

(defn pngdata-from-srting [fs]
  (def zimage (BufferedImage. 256 256 BufferedImage/TYPE_INT_RGB))
  (def g2d (.createGraphics zimage))
  (apply-from-string fs g2d 256 256)
  (def os (ByteArrayOutputStream.))
  (ImageIO/write zimage "png" os)
  (ByteArrayInputStream. (.toByteArray os)))

(defpage [:post "/generate"] {:as user}
  {:status  200
   :headers {
      "Content-Type" "image/png",
      "Content-Disposition" "inline; filename=\"image.png\"",
      "Cache-Control" "public; max-age=60" }
   :body (pngdata-from-srting (:code user))})
