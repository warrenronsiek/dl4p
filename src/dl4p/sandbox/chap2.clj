(ns dl4p.sandbox.chap2
  (:require [uncomplicate.commons.core :refer [with-release]]
           [uncomplicate.fluokitten.core :refer [fmap!]]
           [uncomplicate.neanderthal
             [native :refer [dv dge]]
             [core :refer [mv! mv axpy! scal!]]
             [math :refer [signum exp]]
             [vect-math :refer [fmax! tanh! linear-frac!]]]))

(defn step! [threshold x]
  (fmap! signum (axpy! -1.0 threshold (fmax! threshold x x))))

(let [threshold (dv [ 1 2 3])
      x (dv [0 2 7])]
  (step! threshold x))

(def x (dv 0.3 0.9))
(def w1 (dge 4 2 [0.3 0.6
                  0.1 2.0
                  0.9 3.7
                  0.0 1.0]
             {:layout :row}))
(def threshold (dv 0.7 0.2 1.1 2) )

(step! threshold (mv w1 x))

(def bias (dv 0.7 0.2 1.1 2))