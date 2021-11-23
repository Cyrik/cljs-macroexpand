(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]))

(def lib 'net.clojars.cyrik/cljs-macroexpand)
;; if you want a version of MAJOR.MINOR.COMMITS:
(def version (format "0.1.%s" (b/git-count-revs nil)))

(defn test "Run the tests." [opts]
  (bb/run-tests opts))

(defn clean "clean" [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/clean)))

(defn install "Install the JAR locally." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/install)))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/run-tests)
      (bb/clean)
      (bb/jar)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/deploy)))