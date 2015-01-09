(ns clj-payp.paypal
  (:require [clj-http.client :as client]))

(use 'ring.util.codec)
(use 'clojure.walk)

(def db (client/post "https://api.sandbox.paypal.com/v1/oauth2/token"
  {:basic-auth ["EOJ2S-Z6OoN_le_KS1d75wsZ6y0SFdVsY9183IvxFyZp" "EClusMEUk8e9ihI7ZdVLF5cZ6y0SFdVsY9183IvxFyZp"]
   :headers {:content-type "application/x-www-form-urlencoded"}
   :form-params {:grant_type "client_credentials"}
   :accept :json
   :as :json}))

db

(def body (db :body))

(def access_token (body :access_token))

access_token

(def url-start "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=")
(defn get-response [] (client/post "https://api-3t.sandbox.paypal.com/nvp"
  {:form-params {
                 :grant_type "client_credentials",
                 :USER  "simkesd-facilitator_api1.gmail.com",
                 :PWD "1370346674",
                 :SIGNATURE "AFcWxV21C7fd0v3bYYYRCpSSRl31A9No.r0a.cP9WG88Oh--u6CXyaCO",
                 :METHOD "SetExpressCheckout",
                 :VERSION 93,
                 :PAYMENTREQUEST_0_PAYMENTACTION "SALE",
                 :PAYMENTREQUEST_0_AMT "19.95",
                 :PAYMENTREQUEST_0_CURRENCYCODE "USD",
                 :RETURNURL "http://localhost:3000/",
                 :CANCELURL "http://localhost:3000/about"
                 }
   :accept :json
   :as :clojure}))

(def parsed (:body (get-response)))
parsed

(def opala (keywordize-keys (form-decode (name parsed))))
opala

(defn url-for-redirect-to-paypal
  []
  (do (let [response-body ((get-response) :body)
            parsed-response-body (keywordize-keys
                                    (form-decode (name response-body)))
            token (:TOKEN parsed-response-body)]
        (str url-start token))))

(url-for-redirect-to-paypal)

(defn do-express-checkout [token payer-id] (client/post "https://api-3t.sandbox.paypal.com/nvp"
  {:form-params {
                 :grant_type "client_credentials",
                 :USER  "simkesd-facilitator_api1.gmail.com",
                 :PWD "1370346674",
                 :SIGNATURE "AFcWxV21C7fd0v3bYYYRCpSSRl31A9No.r0a.cP9WG88Oh--u6CXyaCO",
                 :METHOD "DoExpressCheckoutPayment",
                 :VERSION 93,
                 :TOKEN token,
                 :PAYERID payer-id,
                 :PAYMENTREQUEST_0_PAYMENTACTION "SALE",
                 :PAYMENTREQUEST_0_AMT "19.95",
                 :PAYMENTREQUEST_0_CURRENCYCODE "USD",
                 }
   :accept :json
   :as :clojure}))

(defn get-express-checkout-details [token] (client/post "https://api-3t.sandbox.paypal.com/nvp"
  {:form-params {
                 :grant_type "client_credentials",
                 :USER  "simkesd-facilitator_api1.gmail.com",
                 :PWD "1370346674",
                 :SIGNATURE "AFcWxV21C7fd0v3bYYYRCpSSRl31A9No.r0a.cP9WG88Oh--u6CXyaCO",
                 :METHOD "GetExpressCheckoutDetails",
                 :VERSION 93,
                 :TOKEN token,
                 }
   :accept :json
   :as :clojure}))

(get-express-checkout-details "asdfasdf")

(do-express-checkout "asdfasfd" "asdfasdf")
