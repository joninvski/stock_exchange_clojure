(ns stock-exchange.supplier
  (:require [clojure.spec :as spec]
            [stock-exchange.types :as t]))

(def supplier-initial-state
  {::t/agent-type ::supplier
   ::state 0
   ::offers []
   ::matched []})

(defn create-supplier
  "Given an id, creates a supplier agent"
  [id]
  (agent (assoc supplier-initial-state ::t/id id)))

(defn create-n-suppliers
  "Creates n suppliers agents"
  [n]
  (map create-supplier (range n)))

(spec/fdef create-supplier
        :args (spec/cat :id ::t/id)
        :ret #(spec/valid? ::t/agent %))

(spec/fdef create-nsuppliers
        :args (spec/cat :n integer?))

(spec/def ::supplier-state integer?)
