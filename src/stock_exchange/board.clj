(ns stock-exchange.board
  (:require [clojure.spec :as spec]
            [stock-exchange.core :as core]))

(def board-initial-state 
  {::core/agent-type ::bulletin-board 
   ::requests []})

(defn create-bulletin-board
  "Given an id, creates a bulletin-board agent"
  [id]
  (agent (assoc board-initial-state ::core/id id)))

(defn create-n-bulletin-boards
  "Create n bulleting board agents"
  [n]
  (zipmap
   (range n)
   (map create-bulletin-board (range n))))

(spec/fdef create-bulletin-board
        :args (spec/cat :id ::id)
        :ret #(spec/valid? ::core/agent %))

(spec/fdef create-n-bulletin-boards
        :args (spec/cat :n integer?))
