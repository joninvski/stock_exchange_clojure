(ns stock-exchange.requester
  (:require [clojure.spec :as spec]
            [stock-exchange.types :as t]))

(def requester-initial-state
  {::t/agent-type ::requester
   ::suppliers-matched []})

(defn create-requester
  "Given an id, creates a request agent"
  [id]
  (agent (assoc requester-initial-state ::t/id id)))

(defn create-n-requesters
  "Creates n requesters agents"
  [n]
  (map create-requester (range n)))

(spec/fdef create-requester
        :args (spec/cat :id ::t/id)
        :ret #(spec/valid? ::t/agent %))

(spec/fdef create-n-requesters
        :args (spec/cat :n integer?))
