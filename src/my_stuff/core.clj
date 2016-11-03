(ns stock-exchange-agents
  (:require [clojure.string :as str]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]))

;;; map specs
(s/def ::agent-type #{:supplier :requester :bulletin-board})
(s/def ::id integer?)
(s/def ::agent-state integer?)
(s/def ::supplier-state (s/keys :req [::agent-type ::state]))
(s/def ::agent #(= (type %) clojure.lang.Agent))


(def supplier-initial-state {::agent-type :supplier :agent-state 0 ::offers [] ::matched []})
(def requester-initial-state {::agent-type :requester :suppliers-matched []})
(def board-initial-state {:agent-type :bulletin-board :requests []})
(def n-suppliers 1)
(def n-requesters 1)
(def n-boards 1)

(defn create-supplier
  [id]
  (agent (assoc supplier-initial-state ::id id)))

(s/fdef create-supplier
        :args (s/cat :id ::id)
        :ret #(s/valid? ::agent %))

(defn create-requester
  [id]
  (agent (assoc requester-initial-state ::id id)))

(s/fdef create-requester
        :args (s/cat :id ::id)
        :ret #(s/valid? ::agent %))

(defn create-bulletin-board
  [id]
  (agent (assoc board-initial-state ::id id)))

(s/fdef create-bulletin-board
        :args (s/cat :id ::id)
        :ret #(s/valid? ::agent %))


(def suppliers (map create-supplier (range n-suppliers)))

(def requesters (map create-requester (range n-requesters)))

(def bulletin-boards (zipmap
                      (range n-boards)
                      (map create-bulletin-board (range n-boards))))

(defn post-bb
  [bb-state id]
  (update-in bb-state [:requests] conj id))

(defn supplier-evaluate
  [supplier-state topics request-id]
  (update-in supplier-state [:offers] conj request-id))

(defn post
  [id topics]
  (let [matched-bulletin-boards (vals (select-keys bulletin-boards topics))]
    (map #(send % post-bb id) matched-bulletin-boards)
    (map #(send % supplier-evaluate topics id) suppliers)))

(defn -main []
    (post 0 [0])
    (map deref suppliers)
    (map deref (vals bulletin-boards)))
