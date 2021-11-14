(ns dl4p.sandbox.chap1
  (:require
    [uncomplicate.commons.core :refer [with-release]]
    [uncomplicate.neanderthal
     [native :refer [dv dge]]
     [core :refer [mv mv!]]]))

(def x (dv 0.3 1.9))

(def w1)

(with-release [x (dv 0.3 0.9)
               w1 (dge 4 2 [0.3 0.6
                            0.1 2.0
                            3.2 0.3
                            0.1 3.0] {:layout :row})
               w2 (dge 1 4 [0.75 0.15 0.22 0.33])
               h1 (dv 4)
               y (dv 1)]
              (println (mv! w2 (mv! w1 x h1) y)))
