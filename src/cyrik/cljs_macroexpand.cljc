(ns cyrik.cljs-macroexpand
  #?(:clj (:require [cyrik.cljs-macroexpand.expand :as m]))
  #?(:cljs (:require-macros [cyrik.cljs-macroexpand :refer [cljs-macroexpand-all]])))

#?(:clj
   (defmacro cljs-macroexpand-all
     [arg]
     (binding [m/*environment* &env]
       (m/macroexpand-all arg))))

(comment
  (cljs-macroexpand-all '(when true "false"))
  (macroexpand '(when (let [a 5] a) "false"))
  (cljs-macroexpand-all '(when (let [a 5] a) "false"))

  (cljs-macroexpand-all '(cljs-macroexpand-all (let [a 5] a)))
)