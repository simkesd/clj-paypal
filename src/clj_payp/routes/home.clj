(ns clj-payp.routes.home
  (:require [compojure.core :refer :all]
            [clj-payp.layout :as layout]
            [clj-payp.util :as util]
            [noir.response :as resp]
            [clj-payp.paypal :as paypal]
            [clj-payp.db.core :as db]
            [cheshire.core :refer :all]
            [noir.session :as session]))
(defn home-page []
  (layout/render
    "home.html"))

(defn about-page []
  (layout/render "about.html"))

(defn handle-paypal-checkout
  "redirect user to paypal to checkout his items"
  []
    (let
      [amount (db/payment-amount (session/get :user-id))
       redirect-url (paypal/url-for-redirect-to-paypal amount)] ;; pokupi redirect url | upisi inicijalne podatke u bazu | redirektuj
      (resp/redirect redirect-url)))

(defn dashboard
  ""
  []
    (layout/render "store.html" {:items (db/list-items)}))

(defn add-to-cart-bla
  ""
  [json-data]
    (str (type (session/get :user-id))))

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
          (str json-data)
          (db/update-checkout-list-single (java.lang.Integer/parseInt (session/get :user-id)) (:item-id (first json)) (:amount (first json)))
          (recur (rest json) (conj helper (first json)))))))

(defn ids-from-shoping-cart
  "Extract ids from shopping cart items list."
  [shopping-cart]
    (loop [items (:items shopping-cart)
       helper []]
      (if (empty? items)
        ;;true
        helper
        ;;false
        (recur (rest items) (conj helper (:itemId (first items)))))))

(defn cart
  ""
  []
    (layout/render "cart.html" {:items (:items (db/get-shopping-cart-for-user 999))}))

(defn items
  ""
  []
    (db/list-items))

(defn shopping-cart-items
  ""
  []
    (:items (db/get-shopping-cart-for-user 999)))


(defn shopping-cart
  ""
  []
    (db/get-shopping-cart-for-user 999))

(defn handle-paypal-redirect
  "Upisi token i PayerID u bazu, pokupi podatke korisnika i prikazi stranicu za potvrdu uplate!"
  [token PayerID]
    (layout/render "final-paypal.html" {:token token :buyer-details (paypal/buyer-details token)}))

(defn pay
  ""
  [token PayerID]
  (let
      [payment-data (paypal/pay token PayerID (db/payment-amount (session/get :user-id)))]
      (do
        (if (= (:ACK payment-data) "Success")
          ;; if payment was successfull
          (do
            ;;tag payment in database
            (println (str (type (java.lang.Integer/parseInt (session/get :user-id)))))
            (println (str payment-data))
            (println (str (session/get :user-id)))
            (def res (db/add-final-payment-info (java.lang.Integer/parseInt (session/get :user-id))  payment-data))
            (println res)
            (db/mark-shopping-cart-as-finished (:_id (db/get-shopping-cart-for-user 999)))
            (println "jos posle")
            (layout/render "finished.html" {:success true :data payment-data}))
          ;; if payment was not successfull
            (layout/render "finished.html" {:success false :data payment-data})
        ))))


(defn remove-item-from-cart
  ""
  [item-id]
    (str (db/delete-item-from-cart (java.lang.Integer/parseInt (session/get :user-id)) (java.lang.Integer/parseInt item-id))))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/store" [] (dashboard))
  (POST "/add-to-cart" [data] (add-to-cart data))
  (GET "/cart" [] (cart))
  (GET "/cart/remove/:id" [id] (remove-item-from-cart id))
  (GET "/paypal" [] (handle-paypal-checkout))
  (GET "/paypal/checkout" [token PayerID] (handle-paypal-redirect token PayerID))
  (GET "/paypal/pay" [token PayerID] (pay token PayerID))
  (GET "/items" [] (items))
  (GET "/shopping-cart" [] (noir.response/content-type
       "application/javascript" (generate-string (shopping-cart))))
  (GET "/shopping-cart-items" [] (noir.response/content-type
       "application/javascript" (generate-string (shopping-cart-items))))
  (GET "/about" [] (about-page)))
