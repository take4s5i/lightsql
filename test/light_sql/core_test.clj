(ns light-sql.core-test
  (:require [clojure.test :refer :all]
            [light-sql.core :refer :all]
            [light-sql.test-util :as util]))

(deftest parametarize-test
  (testing "return correct results"
    (are [s ps r] (= r ((parametarize "null" s) ps))
         "hoge ${1} ${3}" ["x" "y" "z"] "hoge x z"
         "${1} ${3}" [1 2] "1 null"
         "${1} ${2}" [] "null null")))


(deftest query-test
  (testing "correct results"
    (let [sqls ["select * from developers where devid = ${1}"
                "select * from skills where devid = ${1}"]
          params [[1] [2]]
          devs-expect [{:devid 1 :name "taro tanaka" :birthday "19800101"}
                       {:devid 2 :name "jiro suzuki" :birthday "19791231"}]
          skls-expect [{:devid 1 :techid 1 :added "20050401"}
                       {:devid 1 :techid 2 :added "20060506"}
                       {:devid 2 :techid 1 :added "20000908"}]
          expect [devs-expect skls-expect]]
      (util/init-db util/db-spec)
      (is (= (query util/db-spec sqls params)
             expect)))))

(deftest topological-sort-test
  (testing "sort"
    (are [topos result] (= (topological-sort topos) result)
         {:a [:b :c]
          :b [:c]
          :c []}
         [:c :b :a])))

(deftest create-index-test
  (testing "created correct index"
    (let [tbl [{:devid 1 :techid 1 :added "20050401"}
               {:devid 1 :techid 2 :added "20060506"}
               {:devid 2 :techid 1 :added "20000908"}]
          fkey (juxt :devid )
          expect {[1] [0 1]
                  [2] [2]}]
      (is (= expect (create-index tbl fkey))))))

(deftest join-test
  (testing "correct join"
    (let [tbl1 [{:id 1 :val "one"}
                {:id 2 :val "two"}
                {:id 3 :val "three"}]
          tbl2 [{:id 1 :prop "ichi"}
                {:id 1 :prop "ni"}
                {:id 2 :prop "san"}]
          fkey (juxt :id)
          expect [{:id 1 :val "one" :test [{:id 1 :prop "ichi"}
                                           {:id 1 :prop "ni"}]}
                  {:id 2 :val "two" :test [{:id 2 :prop "san"}]}
                  {:id 3 :val "three" :test nil }]]
      (is (= (join fkey fkey tbl1 tbl2 :test)
             expect)))))


