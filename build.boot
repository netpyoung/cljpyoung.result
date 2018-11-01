(def project
  {:project     'netpyoung/cljpyoung.result
   :version     "0.2.0"
   :description "FIXME: write description"
   :url         "https://github.com/netpyoung/cljpyoung.result"
   :scm         {:url "https://github.com/netpyoung/cljpyoung.result"}
   :license     {"Eclipse Public License"
                 "http://www.eclipse.org/legal/epl-v10.html"}})


(set-env! :resource-paths #{"src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0"]
                            [adzerk/boot-test "1.2.0" :scope "test"]])


(deftask build
  [_ snapshot LOCAL boolean "build local"]
  (task-options!
   pom (if snapshot
         (update-in project [:version] (fn [x] (str x "-SNAPSHOT")))
         project))
  (comp (pom)))

(deftask local
  []
  (comp (build)
        (jar)
        (install)))

(deftask local-snapshot
  []
  (comp (build :snapshot true)
        (jar)
        (install)))

(deftask prepare-push
  []
  (set-env!
   :repositories
   #(conj % ["clojars" {:url "https://clojars.org/repo/"
                        :username (get-sys-env "CLOJARS_USER" :required)
                        :password (get-sys-env "CLOJARS_PASS" :required)}]))
  identity)

(deftask push-release
  []
  (comp (prepare-push)
        (build)
        (jar)
        (push :repo "clojars" :ensure-release true)))

(deftask push-snapshot
  []
  (comp (prepare-push)
        (build :snapshot true)
        (jar)
        (push :repo "clojars" :ensure-snapshot true)))

(require '[adzerk.boot-test :refer [test]])
