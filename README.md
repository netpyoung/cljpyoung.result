# cljpyoung.result

[![Clojars Project](https://img.shields.io/clojars/v/netpyoung/cljpyoung.result.svg)](https://clojars.org/netpyoung/cljpyoung.result)

* I don't want to throw Exception.

## Usage
```
Leiningen/Boot
[netpyoung/cljpyoung.result "0.2.0"]

Clojure CLI/deps.edn
netpyoung/cljpyoung.result {:mvn/version "0.2.0"}

Gradle
compile 'netpyoung:cljpyoung.result:0.2.0'

Maven
<dependency>
  <groupId>netpyoung</groupId>
  <artifactId>cljpyoung.result</artifactId>
  <version>0.2.0</version>
</dependency>
```

``` clojure
(:require [cljpyoung.result :as r])

(require '[cljpyoung.result :as r])
```

``` clojure
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
```

## Ref:
* https://doc.rust-lang.org/std/result/enum.Result.html

## License

Copyright © 2018 netpyoung

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
