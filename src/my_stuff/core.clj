(def supplier-initial-state {:type :supplier :state 0 :offers [] :matched []})
(def requester-initial-state {:type :requester :suppliers-matched []})
(def board-initial-state {:type :bulletin-board :requests []})
(def n-suppliers 1)
(def n-requesters 1)
(def n-boards 1)

(defn create-supplier
  [id]
  (agent (assoc supplier-initial-state :id id)))

(defn create-requester
  [id]
  (agent (assoc requester-initial-state :id id)))

(defn create-bulletin-board
  [id]
  (agent (assoc board-initial-state :id id)))

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
    (map #(send % supplier-evaluate topics id) suppliers))))

(defn -main []
    (post 0 [0])
    (map deref suppliers)
    (map deref (vals bulletin-boards))))

