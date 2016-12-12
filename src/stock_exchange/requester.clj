(ns stock-exchange.requester
  (:require [clojure.spec :as spec]
            [stock-exchange.core :as core]))

(def requester-initial-state
  {::core/agent-type ::requester
   ::suppliers-matched []})

(defn create-requester
  "Given an id, creates a request agent"
  [id]
  (agent (assoc requester-initial-state ::core/id id)))

(defn create-n-requesters
  "Creates n requesters agents"
  [n]
  (map create-requester (range n)))

(spec/fdef create-requester
        :args (spec/cat :id ::core/id)
        :ret #(spec/valid? ::core/agent %))

(spec/fdef create-n-requesters
        :args (spec/cat :n integer?))
