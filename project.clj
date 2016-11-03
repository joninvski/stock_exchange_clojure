(defproject my-stuff "0.1.0-SNAPSHOT"
  :description "AgentStockExchange"
  :url "http://github.com/joninvski/stock_exchange_clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cljfmt "0.5.1"]
                 ]

  :main ^:skip-aot my-stuff.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[cider/cider-nrepl "0.13.0"]])
