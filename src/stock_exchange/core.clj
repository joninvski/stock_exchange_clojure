(ns stock-exchange.core
  (:require [clojure.string :as str]
            [clojure.spec :as s]
            [clojure.tools.logging :as log])
  (:use [clojure.tools.trace :only [trace-ns untrace-ns]]))

;;; map specs
(s/def ::agent-type #{:supplier :requester :bulletin-board})
(s/def ::id integer?)
(s/def ::agent-state integer?)
(s/def ::supplier-state (s/keys :req [::agent-type ::state]))
(s/def ::agent #(= (type %) clojure.lang.Agent))

(def supplier-initial-state {::agent-type ::supplier ::agent-state 0 ::offers [] ::matched []})
(def requester-initial-state {::agent-type ::requester ::suppliers-matched []})
(def board-initial-state {::agent-type ::bulletin-board ::requests []})

(def n-suppliers 2)
(def n-requesters 1)
(def n-boards 1)

(defn create-supplier
  "Given an id, creates a supplier agent"
  [id]
  (agent (assoc supplier-initial-state ::id id)))

(s/fdef create-supplier
        :args (s/cat :id ::id)
        :ret #(s/valid? string? %))

(defn create-requester
  "Given an id, creates a request agent"
  [id]
  (agent (assoc requester-initial-state ::id id)))

(s/fdef create-requester
        :args (s/cat :id ::id)
        :ret #(s/valid? ::agent %))

(defn create-bulletin-board
  "Given an id, creates a bulletin-board agent"
  [id]
  (agent (assoc board-initial-state ::id id)))

(s/fdef create-bulletin-board
        :args (s/cat :id ::id)
        :ret #(s/valid? ::agent %))

(defn create-n-suppliers
  "Creates n suppliers agents"
  [n]
  (map create-supplier (range n)))

(s/fdef create-nsuppliers
        :args (s/cat :n integer?))

(defn create-n-requesters
  "Creates n requesters agents"
  [n]
  (map create-requester (range n)))

(s/fdef create-n-requesters
        :args (s/cat :n integer?))

(defn create-n-bulletin-boards
  "Create n bulleting board agents"
  [n]
  (zipmap
   (range n-boards)
   (map create-bulletin-board (range n-boards))))

(defn create-world 
  [n-suppliers n-requesters n-boards]
  {::suppliers (create-n-suppliers n-suppliers)
   ::requesters (create-n-requesters n-requesters)
   ::bulletin-boards (create-n-bulletin-boards n-boards)})

(defn post-bb
  [bb-state id]
  (update-in bb-state [::requests] conj id))

(defn supplier-evaluate
  [supplier-state topics request-id]
  (update-in supplier-state [::offers] conj request-id))

(defn post
  [id topics world]
  (let [bulletin-boards (::bulletin-boards world)
        suppliers (::suppliers world)
        requesters (::requesters world)
        matched-bulletin-boards (vals (select-keys bulletin-boards topics))]
    (do
      (dorun (map #(send % post-bb id) matched-bulletin-boards))
      (dorun (map #(send % supplier-evaluate topics id) suppliers)))))

(defn add-watcher
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

(def world (atom {}))

(defn add-watchers
  [world]
  (let [suppliers (@world ::suppliers)
        bulletin-boards (vals (@world ::bulletin-boards))
        requesters (@world ::requesters)]
    (dorun (map #(add-watcher % ::offers) suppliers))
    (dorun (map #(add-watcher % ::requests) bulletin-boards))
    (dorun (map #(add-watcher % ::suppliers-matched) requesters))))

(defn -main []
  (swap! world (constantly (create-world n-suppliers n-requesters n-boards)))
  (add-watchers world)
  (post 0 [0] @world))

(defn activate-trace-debug
  [b]
  (if b
    (trace-ns 'stock-exchange.core)
    (untrace-ns 'stock-exchange.core)))
