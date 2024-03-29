(ns website.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "website"]
               (include-css "/css/reset.css" "/css/noir.css")]
              [:body
               [:div#wrapper
                content]]))


