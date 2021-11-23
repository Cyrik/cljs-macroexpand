(ns cyrik.cljs-macroexpand.expand
  (:require [clojure.walk :as walk]
            [cljs.analyzer :as ana]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
 ;; ATTENTION!!, some nasty hacks  ;;
 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Normal `clojure.core/macroexpand-1` works differently when being called from clojure and clojurescript. ;;
;; See: https://github.com/jpmonettas/clojurescript-macro-issue                                            ;;
;; One solution is to use clojure.core/macroexpand-1 when we are in a clojure environment                  ;;
;; and user cljs.analyzer/macroexpand-1 when we are in a clojurescript one.                                ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def ^:dynamic *environment*)

(defn target-from-env
  "Given a env map return :cljs or :clj"
  [env]
  (if (contains? env :js-globals)
    :cljs
    :clj))

(defn normalized-macroexpand-1
  "A version of macroexpand-1 that works for clojure and clojurescript
  given it can tell from *environment* if we are in clojure or clojurescript"
  [form]
  ((case (target-from-env *environment*)
     :cljs (partial ana/macroexpand-1 *environment*)
     :clj  macroexpand) form))

(defn normalized-macroexpand
  "A macroexpand version that uses normalized-macroexpand-1 instead of clojure.core/macroexpand-1"
  [form]
  (let [ex (if (seq? form)
             (normalized-macroexpand-1 form)
             form)]
    (if (identical? ex form)
      form
      (normalized-macroexpand ex))))

(defn merge-meta
  "Non-throwing version of (vary-meta obj merge metamap-1 metamap-2 ...).
  Like `vary-meta`, this only applies to immutable objects. For
  instance, this function does nothing on atoms, because the metadata
  of an `atom` is part of the atom itself and can only be changed
  destructively."
  {:style/indent 1}
  [obj & metamaps]
  (try
    (apply vary-meta obj merge metamaps)
    (catch Exception e obj)))

(defn strip-meta
  "Strip meta from form.
  If keys are provided, strip only those keys."
  ([form] (strip-meta form nil))
  ([form keys]
   (if (and (instance? clojure.lang.IObj form)
            (meta form))
     (if keys
       (with-meta form (apply dissoc (meta form) keys))
       (with-meta form nil))
     form)))

(defn macroexpand-all
  "Like `clojure.walk/macroexpand-all`, but preserves and macroexpands
  metadata. Also store the original form (unexpanded and stripped of
  metadata) in the metadata of the expanded form under original-key."
  [form & [original-key]]
  (let [md (meta form)
        expanded (walk/walk #(macroexpand-all % original-key)
                            identity
                            (if (seq? form)
                              ;; Without this, `macroexpand-all`
                              ;; throws if called on `defrecords`.
                              (try (let [r (normalized-macroexpand form)]
                                     r)
                                   (catch ClassNotFoundException e form))
                              form))]
    (if md
      ;; Macroexpand the metadata too, because sometimes metadata
      ;; contains, for example, functions. This is the case for
      ;; deftest forms.
      (merge-meta expanded
                  (macroexpand-all md)
                  (when original-key
          ;; We have to quote this, or it will get evaluated by
          ;; Clojure (even though it's inside meta).
                    {original-key (list 'quote (strip-meta form))}))

      expanded)))