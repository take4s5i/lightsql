(ns lightsql.core-test
  (:require [clojure.java.jdbc :as sql]
            [clojure.test :refer :all]
            [lightsql.core :refer :all]))

(def db-spec {:subprotocol "sqlite"
              :subname "target/test.db"})

(defn drop-tables [db]
  (doseq [table [:developers :techs :skills]]
    (try
      (sql/db-do-commands
        db
        (sql/drop-table-ddl table))
      (catch Exception _
        ;; ignore
        ))))

(defn create-tables [db]
  (sql/db-do-commands
    db (sql/create-table-ddl
         :developers
         [:devid :int "PRIMARY KEY"]
         [:name "TEXT"]
         :table-spec "")))

(defn load-fixtrues [db]
  (sql/insert!
    db
    :developers
    nil
    [1 "太郎" ]
    [2 "次郎" ]
    [3 "たくお"]))

(defn init-db [db]
  (drop-tables db)
  (create-tables db)
  (load-fixtrues db))

(deftest query-test
  (testing "query with result"
    (let [conn "jdbc:sqlite:target/test.db"
          sql  "select * from developers where devid = ?"
          params [1]
          expect "[{\"name\":\"太郎\",\"devid\":1}]"]
      (Class/forName "org.sqlite.JDBC")
      (init-db db-spec)
      (is (= expect (apply query conn sql params)))))
  (testing "query without result"
    (let [conn "jdbc:sqlite:target/test.db"
          sql  "select * from developers where devid = ?"
          params [4]
          expect "[]"]
      (Class/forName "org.sqlite.JDBC")
      (init-db db-spec)
      (is (= expect (apply query conn sql params))))))
