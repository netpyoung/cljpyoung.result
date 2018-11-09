(ns cljpyoung.result)

(defprotocol IResult)

(defrecord Ok [ok]
  IResult
  Object
  (toString [this]
    (str "<Ok [" ok "]>"))
  clojure.lang.IDeref
  (deref [this] ok)
  clojure.lang.Indexed
  (nth [this index]
    (case index 0 ok 1 nil))
  (nth [this index not-found]
    (case index 0 ok 1 nil not-found)))

(defrecord Err [err]
  IResult
  Object
  (toString [this]
    (str "<Err [" err "]>"))
  clojure.lang.IDeref
  (deref [this] err)
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
  (deref ok))

(defn err! [err]
  (when-not (err? err)
    (throw (ex-info "err!" {:cause :err! :err err})))
 (deref err))

(defmacro result [expr]
  `(try
     (ok ~expr)
     (catch Exception e#
       (err e#))))

(defn result? [o]
  (satisfies? IResult o))

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
