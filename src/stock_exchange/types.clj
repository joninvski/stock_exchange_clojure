(ns stock-exchange.types
  (:require [clojure.spec :as spec])
  )

(spec/def ::agent-type #{:supplier :requester :bulletin-board})
(spec/def ::id integer?)
(spec/def ::agent #(= (type %) clojure.lang.Agent))
