(ns clj-payp.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all]
              monger.json)
  (:import org.bson.types.ObjectId))


;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost
(defonce db (let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://127.0.0.1/clj-payp")
                  {:keys [conn db]} (mg/connect-via-uri uri)]
              db))

(def users-collection "users")
(def shopping-cart-collection "checkout-lists")
(def items-collection "items")

;; ******************
;; USERS
;; ******************
(defn create-user [user]
  (mc/insert db users-collection user))

(defn update-user [id first-name last-name email]
  (mc/update db users-collection {:id id}
             {$set {:first_name first-name
                    :last_name last-name
                    :email email}}))

(defn get-user [id]
  (mc/find-one-as-map db "users" {:id id}))

;; ******************
;; SHOPPING CART
;; ******************
(defn item-is-added?
  "Returns nil if item user does not have item in his shopping-cart, otherwise it returns checkout list."
  [user-id item-id]
    (mc/find-one db shopping-cart-collection {:userId user-id :finished "false" :items {$elemMatch {:id item-id}}}))

(defn shopping-cart-exists?
  "Returns nil if checkout list for user does not exist, or checkout list if it does."
  [user-id]
    (mc/find-one db shopping-cart-collection {:userId user-id :finished "false"}))

(defn item-by-id
  ""
  [item-id]
    (mc/find-one-as-map db items-collection {:id item-id}))

(defn update-checkout-list-single
  "Insert or update checkout list with given amount of items."
  [user-id item-id amount]
    (if (shopping-cart-exists? user-id)
      ; user already has checklist
      (if (item-is-added? user-id item-id)
           ;; if item already exist in list
           (mc/update db shopping-cart-collection
                {:userId user-id :finished "false" :items {$elemMatch {:id item-id}}}
                {$inc {"items.$.amount" amount "items.$.id" 0}})

          ;; if item doesn't exist in list
          (mc/update db shopping-cart-collection
                {:userId user-id :finished "false"}
                {$addToSet {:items (assoc (item-by-id item-id) :amount amount)}}))

      ; user has not created checklist yet
      (mc/insert db shopping-cart-collection
                     {:userId user-id :finished "false" :items [(assoc (item-by-id item-id) :amount amount)]})))

(defn get-shopping-cart-for-user
  ""
  [user-id]
    (mc/find-one-as-map db shopping-cart-collection {:userId user-id :finished "false"}))

(defn items-by-id
  "Get items with requested ids"
  [ids-amounts-vector]
    (mc/find-maps db items-collection
                  {$or
                       (loop
                         [helper ids-amounts-vector
                          vector []]
                         (if (empty? helper)
                           ;; true
                           vector
                           ;;false
                           (do
                             (recur (rest helper) (conj vector {:id (:itemId (first helper))})))))}))

(defn mark-shopping-cart-as-finished
  ""
  [object-id-string]
    (mc/update db shopping-cart-collection {:_id (ObjectId. (str object-id-string))} {$set {:finished "true"}}))

(defn payment-amount
  "Calculate how much does user has to pay. Info are from his current shopping cart!"
  [user-id]
  (let [items (:items (get-shopping-cart-for-user 999))]
    (loop [helper items
           sum 0]
      (if (empty? helper)
        sum
      (recur (rest helper) (+ sum (* (:amount (first helper)) (:price (first helper)))))))))

(defn add-final-payment-info
  ""
  [user-id paypal-info]
    (mc/update db shopping-cart-collection
                {:userId user-id :finished "false"}
                {$set {:paypalInfo paypal-info}}))
;(reduce + (map #(+ (* (:amount %) (:price %)) ) s))

(defn delete-item-from-cart
  ""
  [user-id item-id]
    (mc/update db shopping-cart-collection {:userId user-id :finished "false"} {$pull {:items {:id item-id}}}))


;(mc/find-one db shopping-cart-collection {:userId user-id :finished "false" :items {$elemMatch {:id item-id}}})

;; ******************
;; ITEMS
;; ******************
(defn list-items
  "List all items from database."
  []
    (mc/find-maps db items-collection {}))
