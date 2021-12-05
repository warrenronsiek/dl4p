(ns dl4p.sandbox.chap3
  (:require [uncomplicate.commons.core :refer [with-release]]
            [uncomplicate.commons :refer [Releaseable]]
            [uncomplicate.fluokitten.core :refer [fmap!]]
            [uncomplicate.neanderthal
             [native :refer [dv dge]]
             [core :refer [mv! mv axpy! scal!]]
             [math :refer [signum exp]]
             [vect-math :refer [fmax! tanh! linear-frac!]]])
  (:import (clojure.lang IFn)))

(defprotocol Parameters
  (weights [this])
  (bias [this]))

(deftype FullyConnectedInterface [w b h active-fn]
  Releaseable)