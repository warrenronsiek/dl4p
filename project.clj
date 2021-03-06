(defproject dl4p "0.0.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :main dl4p.core
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [nrepl "0.8.3"]
                 [cli-matic/cli-matic "0.4.3"]
                 [clj-commons/clj-yaml "0.7.1"]
                 [com.taoensso/timbre "5.1.2"]
                 [com.cognitect.aws/api "0.8.524"]
                 [com.cognitect.aws/endpoints "1.1.12.93"]
                 [com.cognitect.aws/ec2 "814.2.1008.0"]]
  :profiles {:uberjar  {:main    dl4p.core
                        :init-ns dl4p.core
                        :aot     [dl4p.core]}
             :bindings {:init-ns      dl4p.sandbox.chap1
                        :dependencies [[uncomplicate/neanderthal "0.43.1"]
                                       [org.bytedeco/mkl-platform-redist "2020.3-1.5.4"]]}}
  :jar-exclusions [#".*sandbox.*"]
  :uberjar-exclusions [#".*sandbox.*"]
  :repl-options {:init-ns dl4p.core})