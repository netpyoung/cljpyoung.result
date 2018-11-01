(ns cljpyoung.result-test
  (:require [clojure.test :refer :all])
  (:require [clojure.spec.alpha :as s])
  (:require [cljpyoung.result :as r]))


(deftest ok-err
  (is (= (r/ok 1) (r/ok 1)))
  (is (= (r/err 1) (r/err 1)))
  (is (r/ok? (r/ok 1)))
  (is (r/err? (r/err 1))))


(deftest ok-err2
  (is (= 1 (r/ok! (r/ok 1))))
  (is (= 1 (r/err! (r/err 1))))
  (is (= 1 @(r/ok 1)))
  (is (= 1 @(r/err 1))))


(deftest let-result-simple
  (is (r/ok? (r/let-result :result 1)))
  (is (r/err? (r/let-result :result (/ 1 0)))))


(deftest let-result-normal
  (is (= (r/ok 1)
         (r/let-result [[v err] (r/ok "Ok")]
           :result 1)))
  (is (r/err?
       (r/let-result [[v err] (r/ok "Ok")]
         :result (throw (Exception. "")))))
  (is (= (r/ok 1)
         (r/let-result [[v err] (r/err "Err")]
           :result 1)))
  (is (= (r/err "Err")
         (r/let-result [[v err] (r/err "Err")]
           err
           :result 1)))
  (is (= (r/ok nil)
         (r/let-result [[v err] (r/ok 1)]
           err
           [[v err] (r/ok 2)]
           err
           [[v err] (r/ok 3)]
           :result
           err))))


(deftest let-result-complex
  (is (= (r/ok 100)
         (r/let-result [[v err] (r/ok 1)]
           (do 1
               2
               3
               4
               5)
           [[v err] (do 1
                         2
                         3
                         4
                         5
                         (r/let-result [[v err] (r/ok 100)]
                           :result v))]
           err
           :result v)))
  (testing "throwing when inside let-result binding to [ok err],  will be catched by err"
    (is (r/err?
         (r/let-result [[v err] (r/ok 1)]
           (do 1
               2
               3
               4
               5)
           [[v err] (do 1
                         2
                         3
                         4
                         5
                         (r/let-result [[v err] (r/err 100)]
                           (throw (Exception. "WTF"))
                           :result v))]
           err
           :result v)))
    (is (r/err?
         (r/let-result [[v err] (r/ok 1)]
           (do 1
               2
               3
               4
               5)
           [[v err] (do 1
                         2
                         (throw (Exception. "WTF"))
                         4
                         5
                         (r/let-result [[v err] (r/ok 100)]
                           :result v))]
           err
           :result v)))))
