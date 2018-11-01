(ns cljpyoung.result)

(declare ok!)
(declare err!)

(defrecord Ok [ok]
  Object
  (toString [this]
    (str "<Ok [" ok "]>"))
  clojure.lang.IDeref
  (deref [this] (ok! this))
  clojure.lang.Indexed
  (nth [this index]
    (case index 0 ok 1 nil))
  (nth [this index not-found]
    (case index 0 ok 1 nil not-found)))

(defrecord Err [err]
  Object
  (toString [this]
    (str "<Err [" err "]>"))
  clojure.lang.IDeref
  (deref [this] (err! this))
  clojure.lang.Indexed
  (nth [this index]
    (case index 0 nil 1 this))
  (nth [this index not-found]
    (case index 0 nil 1 this not-found)))


(defmethod print-method Ok [v ^java.io.Writer w]
  (.write w (str v)))

(defmethod print-method Err [v ^java.io.Writer w]
  (.write w (str v)))

(defn ok [v] (Ok. v))

(defn err [v] (Err. v))

(defn ok? [ok] (instance? Ok ok))

(defn err? [err] (instance? Err err))

(defn ok! [ok]
  (when-not (ok? ok)
    (throw (ex-info "ok!" {:cause :ok! :ok ok})))
  (:ok ok))

(defn err! [err]
  (when-not (err? err)
    (throw (ex-info "err!" {:cause :err! :err err})))
 (:err err))

(defmacro result [x]
  `(try
     (let [ret# ~x]
       (ok ret#))
     (catch Exception e#
       (err e#))))

(defmacro let-result [l & body]
  (if (= :result l)
    `(result (do ~@body))
    (let [[fl sl] l
          fb (first body)
          nb (next body)]
      (if (= :result fb)
        `(let [~fl ~sl] (result (do ~@nb)))
        `(let [r# (try ~sl (catch Exception e# (err e#))), ~fl r#]
           (if (err? r#)
             ~fb
             (do
               (let-result ~@nb))))))))
