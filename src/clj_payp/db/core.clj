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

;; ******************
;; USERS
;; ******************

(def users-collection "users")
(def checkout-lists-collection "checkout-lists")

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

(defn update-checkout-list-single
  "Insert or update checkout list"
  [user-id item-id]
     (mc/update db checkout-lists-collection {:id user-id :finished "false"} {$set {:item-id item-id}} {:upsert true}))

(defn update-checkout-list
  ""
  [user-id item-ids-array]
    (mc/update db checkout-lists-collection {:id user-id :finished "false"} {$addToSet {:item-ids ["proba"]}} {:upsert true}))

(update-checkout-list 18 [39,39, 19])

(defn get-checkout-list-for-user
  ""
  [user-id]
    (mc/find-maps db checkout-lists-collection {:id user-id :finished "false"}))

(get-checkout-list-for-user 999)

(defn mark-checkout-list-as-finished
  ""
  [object-id-string]
    (mc/update db checkout-lists-collection {:_id (ObjectId. object-id-string)} {$set {:finished "true"}}))

(defn get-checkout-list-for-user [user-id]
   (mc/find db checkout-lists-collection {:user-id user-id}))


(mc/find-maps db checkout-lists-collection)

(mc/find-maps db users-collection)

(mc/find-one-as-map db users-collection {:id "999"})
