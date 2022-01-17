(ns dl4p.sandbox.chap4
  (:require [uncomplicate.commons.core :refer [with-release Releaseable release let-release]]
            [uncomplicate.fluokitten.core :refer [fmap!]]
            [uncomplicate.neanderthal
             [native :refer [dv dge]]
             [core :refer [mv! mv mm mm! axpy! scal! transfer! rk! entry!]]
             [math :refer [signum exp]]
             [vect-math :refer [fmax! tanh! linear-frac!]]])
  (:import (clojure.lang IFn)))

(def x (dge 2 1 [0.3 0.9]))
(def w1 (dge 4 2 [0.3 0.6
                  0.1 2.0
                  0.9 3.7
                  0.0 1.0]
             {:layout :row}))

(mm w1 x)

(defn sigmoid! [x] (linear-frac! 0.5 (tanh! (scal! 0.5 x)) 0.5))

(def bias-vector (dv 0.7 0.2 1.1 2))
(sigmoid! (axpy! -1.0 bias-vector (mm w1 x)))               ; throws error

(def bias-matrix (dge 4 1 [0.7 0.2 1.1 2]))
(sigmoid! (axpy! -1.0 bias-matrix (mm w1 x)))

(let-release [a (dge 3 2 (repeat 6 1000))]
             (with-release [x (dv 1 2 3)
                            y (dv 20 30)]
                           (rk! 2 x y a)))

(let-release [a (dge 3 2 (repeat 6 1000))]
             (with-release [x (dv 1 2 3)
                            ones (entry! (dv 2) 1)]
                           (rk! x ones a)))