(ns clj-payp.routes.home
  (:require [compojure.core :refer :all]
            [clj-payp.layout :as layout]
            [clj-payp.util :as util]
            [noir.response :as resp]
            [clj-payp.paypal :as paypal]))
(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn handle-paypal
  []
    (let [redirect-url (paypal/url-for-redirect-to-paypal)] ;; pokupi redirect url | upisi inicijalne podatke u bazu | redirektuj
      (resp/redirect redirect-url)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/paypal" [] (handle-paypal))
  (GET "/about" [] (about-page)))
