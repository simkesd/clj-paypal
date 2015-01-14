(ns clj-payp.db.core
    (:require [monger.core :as mg]
              [monger.collection :as mc]
              [monger.operators :refer :all])
  (:import org.bson.types.ObjectId))


;; Tries to get the Mongo URI from the environment variable
;; MONGOHQ_URL, otherwise default it to localhost
(defonce db (let [uri (get (System/getenv) "MONGOHQ_URL" "mongodb://127.0.0.1/clj-payp")
                  {:keys [conn db]} (mg/connect-via-uri uri)]
              db))

(def users-collection "users")
(def checkout-lists-collection "checkout-lists")

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
;; CHECKOUTS
;; ******************
(defn check-if-item-is-added
  "Returns nil if item user does not have item in his checkout list, otherwise it returns checkout list."
  [user-id item-id]
    (mc/find-one db checkout-lists-collection {:userId user-id :finished "false" :items {$elemMatch {:itemId item-id}}}))

(check-if-item-is-added 999 3)

(defn check-if-checklist-exists
  "Returns nil if checkout list for user does not exist, or checkout list if it does."
  [user-id]
    (mc/find-one db checkout-lists-collection {:userId user-id :finished "false"}))

(defn update-checkout-list-single
  "Insert or update checkout list with given amount of items."
  [user-id item-id amount]
    (if (check-if-checklist-exists user-id)
      ; user already has checklist
      (if (check-if-item-is-added user-id item-id)
           ;; if item already exist in list
           (mc/update db checkout-lists-collection
                {:userId user-id :finished "false" :items {$elemMatch {:itemId item-id}}}
                {$inc {"items.$.amount" amount "items.$.itemId" 0}})

          ;; if item doesn't exist in list
          (mc/update db checkout-lists-collection
                {:userId user-id :finished "false"}
                {$addToSet {:items {:itemId item-id :amount amount}}}))

      ; user has not created checklist yet
      (mc/insert db checkout-lists-collection
                     {:userId user-id :finished "false" :items [{:itemId item-id :amount amount}]})))

(defn get-checkout-list-for-user
  ""
  [user-id]
    (mc/find-maps db checkout-lists-collection {:id user-id :finished "false"}))

(defn mark-checkout-list-as-finished
  ""
  [object-id-string]
    (mc/update db checkout-lists-collection {:_id (ObjectId. object-id-string)} {$set {:finished "true"}}))

(defn get-checkout-list-for-user [user-id]
   (mc/find db checkout-lists-collection {:user-id user-id}))


;(mc/find-maps db checkout-lists-collection)
;(mc/find-maps db users-collection)
;(mc/find-one-as-map db users-collection {:id "999"})
