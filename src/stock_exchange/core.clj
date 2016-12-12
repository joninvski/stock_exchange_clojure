(ns stock-exchange.core
  (:require [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clojure.spec :as spec]
            [stock-exchange.supplier :as s]
            [stock-exchange.requester :as r]
            [stock-exchange.board :as b]
            )
  (:use [clojure.tools.trace :only [trace-ns untrace-ns]]))

(spec/def ::agent-type #{:supplier :requester :bulletin-board})
(spec/def ::id integer?)
(spec/def ::agent #(= (type %) clojure.lang.Agent))

; number of suppliers at startup 
(def n-suppliers 2)

; number of requesters at start up
(def n-requesters 1)

; number of bulletin-boards at start up
(def n-boards 1)

; The entire world
(def world (atom {}))

(defn create-world
  "Create the initial world"
  [n-suppliers n-requesters n-boards]
  {::suppliers (s/create-n-suppliers n-suppliers)
   ::requesters (r/create-n-requesters n-requesters)
   ::bulletin-boards (b/create-n-bulletin-boards n-boards)})

(defn post-bb
  "Post request in bulletin-board"
  [bb-state id]
  (update-in bb-state [::requests] conj id))

(defn supplier-evaluate
  "Function of the supplier to evaluate a request"
  [supplier-state topics request-id]
  (update-in supplier-state [::offers] conj request-id))

(defn post
  "Post a request"
  [id topics world]
  (let [bulletin-boards (::bulletin-boards world)
        suppliers (::suppliers world)
        requesters (::requesters world)
        matched-bulletin-boards (vals (select-keys bulletin-boards topics))]
    (do
      (dorun (map #(send % post-bb id) matched-bulletin-boards))
      (dorun (map #(send % supplier-evaluate topics id) suppliers)))))

(defn add-watcher
  "Debug watcher to agent to show values changing "
  [supplier field-to-watch]
  (add-watch supplier
             :key
             (fn [k r o n]
               (log/info
                (format "%s\t%d\tOLD: %s\tNEW: %s"
                        ((str/split (str (@r ::agent-type)) #"/") 1)
                        (o ::id)
                        (o field-to-watch)
                        (n field-to-watch))))))

;;;;; Debugging 
(defn add-watchers
  [world]
  (let [suppliers (@world ::suppliers)
        bulletin-boards (vals (@world ::bulletin-boards))
        requesters (@world ::requesters)]
    (dorun (map #(add-watcher % ::offers) suppliers))
    (dorun (map #(add-watcher % ::requests) bulletin-boards))
    (dorun (map #(add-watcher % ::suppliers-matched) requesters))))

(defn activate-trace-debug
  [b]
  (if b
    (trace-ns 'stock-exchange.core)
    (untrace-ns 'stock-exchange.core)))
;;; Debugging

(defn -main []
  (swap! world (constantly (create-world n-suppliers n-requesters n-boards)))
  (add-watchers world)
  (post 0 [0] @world))

