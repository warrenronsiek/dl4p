(ns dl4p.sandbox.chap3
  (:require [uncomplicate.commons.core :refer [with-release Releaseable release let-release]]
            [uncomplicate.fluokitten.core :refer [fmap!]]
            [uncomplicate.neanderthal
             [native :refer [dv dge]]
             [core :refer [mv! mv axpy! scal! transfer!]]
             [math :refer [signum exp]]
             [vect-math :refer [fmax! tanh! linear-frac!]]])
  (:import (clojure.lang IFn)))

(defprotocol Parameters
  (weights [this])
  (bias [this]))

(deftype FullyConnectedInterface [w b h active-fn]
  Releaseable
  (release [_]
    (release w)
    (release b)
    (release h))
  Parameters
  (weights [this] w)
  (bias [this] b)
  IFn
  (invoke [_ x] (active-fn b (mv! w x h))))

(defn fully-connected [active-fn in-dim out-dim]
  (let-release [w (dge out-dim in-dim)
                bias (dv out-dim)
                h (dv out-dim)]
               (->FullyConnectedInterface w bias h active-fn)))

(defn activ-sigmoid! [bias x]
  (axpy! -1 bias x)
  (linear-frac! 0.5 (tanh! (scal! 0.5 x)) 0.5))

(defn activ-tanh! [bias x]
  (tanh! (axpy! -1.0 bias x)))

(with-release [x (dv 0.3 0.9)
               layer-1 (fully-connected activ-sigmoid! 2 4)]
              (transfer! [0.3 0.1 0.9 0.0 0.6 2.0 3.7 1.0] (weights layer-1))
              (transfer! (dv 0.7 0.2 1.1 2) (bias layer-1))
              (println (layer-1 x)))