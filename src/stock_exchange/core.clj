(ns stock-exchange.core
  (:require [clojure.string :as str]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as core-appenders]
            [stock-exchange.supplier :as s]
            [stock-exchange.requester :as r]
            [stock-exchange.board :as b]
            [stock-exchange.world :as w]
            [stock-exchange.types :as t])
  (:use [clojure.tools.trace :only [trace-ns untrace-ns]]
        [aprint.core :only [aprint]])
  (:gen-class))

; set log level
(def log-file-name "log.txt")
(def log-config
  {:level :debug
   :appenders {:println (core-appenders/println-appender {:stream :auto})
               :spit (core-appenders/spit-appender {:fname "./log.log"})}})
(log/set-config! log-config)

; number of suppliers at startup
(def n-suppliers 2)

; number of requesters at start up
(def n-requesters 0)

; number of bulletin-boards at start up
(def n-boards 1)

; The entire world
(def world (atom {}))

(defn supplier-accepts
  []
  (= (rand-int 2) 1))

(defn supplier-evaluate
  "Function of the supplier to evaluate a request"
  [supplier-state topics request-id]
  (let [supplier-id (supplier-state ::t/id)]
    (if (supplier-accepts)
      (do
        (log/info "Supplier" supplier-id "accepts")
        (update-in supplier-state [::offers] conj request-id))
      (do
        (log/info "Supplier" supplier-id "does not accept")
        supplier-state))))

(defn post
  "Post a request"
  [request topics world]
  (let [bulletin-boards (w/boards world)
        suppliers (w/suppliers world)
        requesters (w/requesters world)
        matched-boards (w/boards-with-topics topics world)]
    (do
      (w/add-request request requesters world)
      (w/register-in-boards request matched-boards)
      (w/offer-to-suppliers request topics suppliers supplier-evaluate))))

;;;;; Debugging
(defn add-watcher
  "Debug watcher to agent to show values changing "
  [supplier field-to-watch]
  (add-watch supplier
             :key
             (fn [k r o n]
               (log/debug
                (format "%s\t%d\tOLD: %s\tNEW: %s"
                        ((str/split (str (@r ::t/agent-type)) #"/") 1)
                        (o ::id)
                        (o field-to-watch)
                        (n field-to-watch))))))

(defn add-watchers
  [world]
  (let [suppliers (w/suppliers world)
        bulletin-boards (w/boards world)
        requesters (w/requesters world)]
    (dorun (map #(add-watcher % ::w/offers) suppliers))
    (dorun (map #(add-watcher % ::w/requests) bulletin-boards))
    (dorun (map #(add-watcher % ::w/suppliers-matched) requesters))))

(defn activate-trace-debug
  [b]
  (if b
    (trace-ns 'stock-exchange.core)
    (untrace-ns 'stock-exchange.core)))

(defn inspect
  [world]
  (aprint @world))
;;; End of debugging

(def topics [:A :B :C])

(defn reset-world!
  []
  (swap! world (constantly (w/create-world topics 1 0))))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn ex
  []
  (let [requester (r/create-requester (uuid))
        topic (rand-nth topics)]
    (post requester [topic] world)))

(defn -main []
  (reset-world!)
  (add-watchers world)
  (ex))
