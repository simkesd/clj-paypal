(ns clj-payp.db.schema
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :refer [file]]
            [noir.io :as io]))

(def db-store (str (.getName (file ".")) "/site.db"))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname db-store
              :user "sa"
              :password ""
              :make-pool? true
              :naming {:keys clojure.string/lower-case
                       :fields clojure.string/upper-case}})

(def db-transactions {:payment_status ""
                      :payment_amount ""
                      :payment_currency ""
                      :payer_email ""
                      :receiver_email ""
                      :transaction_id
                      :transaction_status
                      :ip_address
                      :created_at
                      :updated_at})
