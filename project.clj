(defproject stock-exchange "0.1.0-SNAPSHOT"
  :description "AgentStockExchange"
  :url "http://github.com/joninvski/stock_exchange_clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clojure-future-spec "1.9.0-alpha14"]
                 [org.clojure/test.check "0.9.0"] 
                 [jonase/eastwood "0.2.3"]
                 [com.taoensso/timbre "4.7.4"]
                 [org.clojure/tools.trace "0.7.9"]
                 ]
  :repl-options {:nrepl-middleware [cider.nrepl.middleware.pprint/wrap-pprint]}

  :main ^:skip-aot stock-exchange.core 
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :plugins [[cider/cider-nrepl "0.14.0"]
            [cljfmt "0.5.6"]
            [jonase/eastwood "0.2.3"]])
