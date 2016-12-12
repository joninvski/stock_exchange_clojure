(ns stock-exchange.board
  (:require [clojure.spec :as spec]
            [stock-exchange.types :as t]))

(def board-initial-state
  {::t/agent-type ::bulletin-board
   ::requests []})

(defn create-bulletin-board
  "Given an id, creates a bulletin-board agent"
  [id]
  (agent (assoc board-initial-state ::t/id id)))

(defn create-board-per-topic
  "Create a board per topic"
  [topics]
  (zipmap
   topics
   (map create-bulletin-board topics)))

(defn register-request-in-bb
  "Registers in bulletin-board"
  [bb-value request]
  (update-in bb-value [::requests] conj request))

(spec/fdef create-bulletin-board
           :args (spec/cat :id ::id)
           :ret #(spec/valid? ::t/agent %))

(spec/fdef create-n-bulletin-boards
           :args (spec/cat :n integer?))
