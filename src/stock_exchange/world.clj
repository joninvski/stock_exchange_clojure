(ns stock-exchange.world
  (:require [clojure.spec :as spec]
            [stock-exchange.supplier :as s]
            [stock-exchange.board :as b]
            [stock-exchange.requester :as r]
            [stock-exchange.types :as t]))

(defn suppliers
  [world]
  @(@world ::suppliers))

(defn indexed-boards
  [world]
  @(@world ::bulletin-boards))

(defn boards
  [world]
  (vals (indexed-boards world)))

(defn requesters
  [world]
  @(@world ::requesters))

(defn add-request
  [request requesters world]
  (swap! (@world ::requesters) conj request))

(defn boards-with-topics
  [topics world]
  (vals (select-keys (indexed-boards world) topics)))

(defn create-world
  "Create the initial world"
  [n-suppliers n-requesters n-boards]
  {::suppliers (atom (s/create-n-suppliers n-suppliers))
   ::requesters (atom (r/create-n-requesters n-requesters))
   ::bulletin-boards (atom (b/create-n-bulletin-boards n-boards))})
