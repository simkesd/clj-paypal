(ns clj-payp.routes.home
  (:require [compojure.core :refer :all]
            [clj-payp.layout :as layout]
            [clj-payp.util :as util]
            [noir.response :as resp]
            [clj-payp.paypal :as paypal]
            [clj-payp.db.core :as db]
            [cheshire.core :refer :all]))
(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn handle-paypal-checkout
  []
    (let [redirect-url (paypal/url-for-redirect-to-paypal)] ;; pokupi redirect url | upisi inicijalne podatke u bazu | redirektuj
      (resp/redirect redirect-url)))

(defn handle-paypal-redirect
  "Upisi token i PayerID u bazu, pokupi podatke korisnika i prikazi stranicu za potvrdu uplate!"
  [token PayerID]
    (layout/render "final-paypal.html" {:token token}))

(defn dashboard
  ""
  []
    (layout/render "dashboard.html"))

(defn add-to-cart
  ""
  [json-data]
    (loop
      [json (parse-string json-data true)
       helper []]
      (if (empty? json)
        (do
          (str helper))
        (do
          (db/update-checkout-list-single (:id (first json)) 15 20)
          (recur (rest json) (conj helper (type json)))))))

(defn cart
  ""
  []
    (layout/render "cart.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/dashboard" [] (dashboard))
  (POST "/add-to-cart" [data] (add-to-cart data))
  (GET "/cart" [] (cart))
  (GET "/paypal" [] (handle-paypal-checkout))
  (GET "/paypal/checkout" [token PayerID] (handle-paypal-redirect token PayerID))
  (GET "/about" [] (about-page)))
