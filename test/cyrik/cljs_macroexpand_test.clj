(ns cyrik.cljs-macroexpand-test
  (:require [clojure.test :refer :all]
            [cyrik.cljs-macroexpand :refer :all]))

(deftest cljs-macroexpand-all-test
  (testing "works with basic macros"
    (is (= '(if true (do "false"))
           (cljs-macroexpand-all '(when true "false")))))
  
  (testing "works with itself"
    (is (= '(let* [a 5] a)
           (cljs-macroexpand-all '(cljs-macroexpand-all (let [a 5] a)))))))
