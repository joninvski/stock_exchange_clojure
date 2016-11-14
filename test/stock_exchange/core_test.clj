(ns stock-exchange.core-test
  (:require [clojure.test :refer :all]
            [stock-exchange.core :refer :all]
            [clojure.spec :as s]
            [clojure.spec.test :as stest]))

(s/fdef create-supplier
        :args (s/cat :id ::id)
        :ret string?)
(s/exercise-fn `create-supplier)
; (deftest a-test
;   (testing "Specs are passing"
;     (let [results (->>
;                    (stest/enumerate-namespace 'stock-exchange.core)
;                    stest/check
;                    stest/summarize-results)
;           passed (results :check-passed)
;           checked (results :total)]
;       (is (= passed checked)))))
