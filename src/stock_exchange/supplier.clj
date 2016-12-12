(ns stock-exchange.supplier
  (:require [clojure.spec :as spec]
            [stock-exchange.core :as core]))

(def supplier-initial-state
  {::core/agent-type ::supplier
   ::state 0
   ::offers []
   ::matched []})

(defn create-supplier
  "Given an id, creates a supplier agent"
  [id]
  (agent (assoc supplier-initial-state ::core/id id)))

(defn create-n-suppliers
  "Creates n suppliers agents"
  [n]
  (map create-supplier (range n)))

(spec/fdef create-supplier
        :args (spec/cat :id ::core/id)
        :ret #(spec/valid? ::core/agent %))

(spec/fdef create-nsuppliers
        :args (spec/cat :n integer?))

(spec/def ::supplier-state integer?)
