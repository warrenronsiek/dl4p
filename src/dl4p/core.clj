(ns dl4p.core
  (:require [cli-matic.core :refer [run-cmd]]
            [clj-yaml.core :as yaml]
            [dl4p.ec2-env :refer [create-instance]])
  (:gen-class))


(def CONFIGURATION
  {:command     "dl4p-tools"
   :description "manages your neanderthal instances"
   :version     "0.0.1"
   :subcommands [{:command     "create-instance"
                  :short       "cc"
                  :description ["duh"]
                  :opts        [{:option "conf" :short "c" :type :slurp}]
                  :runs        (fn [{:keys [conf]}] (create-instance (yaml/parse-string conf)))}]})


(defn -main [& args] (run-cmd args CONFIGURATION))