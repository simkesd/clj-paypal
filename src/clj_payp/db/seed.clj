(ns clj-payp.db.seed
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              monger.json)
  (:import org.bson.types.ObjectId))


;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost
;;(defonce db (let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://127.0.0.1/clj-payp-test")
;;                  {:keys [conn db]} (mg/connect-via-uri uri)]
;;             db))


(def conn (mg/connect))
(def db (mg/get-db conn "clj-payp"))

(def users-collection "users")
(def shopping-cart-collection "checkout-lists")
(def items-collection "items")



(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test-milos")]
    (mc/insert-and-return db "documents" {:name "John" :age 30})
      )


(defn create-items
  ""
  []
    (do
      (mc/insert-and-return db "items" {
      :id 2
      :name "Shirt"
      :price 2
      :description "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."
      :img_name "clj-shirt2.jpg"})
      (mc/insert-and-return db "items" {
      :id 3
      :name "Hoodie"
      :price 2
      :description "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."
      :img_name "clj-hoodie.jpg"})
      (mc/insert-and-return db "items" {
      :id 1
      :name "Badge"
      :price 2
      :description "Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui."
      :img_name "clj-badge.jpg"})))
