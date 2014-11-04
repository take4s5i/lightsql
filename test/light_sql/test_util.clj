(ns light-sql.test-util
  (:require [clojure.java.jdbc :as sql]))

(def db-spec {:subprotocol "sqlite"
              :subname "target/test_db"})

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
;; developers table
  (sql/db-do-commands
    db (sql/create-table-ddl
         :developers
         [:devid :int "PRIMARY KEY"]
         [:name "TEXT"]
         [:birthday "TEXT"]
         :table-spec ""))
;; techs table
  (sql/db-do-commands
    db (sql/create-table-ddl
         :techs
         [:techid :int]
         [:name "TEXT"]
         :table-spec ""))
;; skills table
  (sql/db-do-commands
    db (sql/create-table-ddl
         :skills
         [:devid :int]
         [:techid :int]
         [:added "TEXT"]
         :table-spec "")))

(defn load-fixtrues [db]
;; insert to developers table
  (sql/insert!
    db
    :developers
    nil
    [1 "taro tanaka" "19800101"]
    [2 "jiro suzuki" "19791231"]
    [3 "takio shibuimaru" "19850501"])
;; insert to techs table
  (sql/insert!
    db
    :techs
    nil
    [1 "clojure"]
    [2 "ruby"]
    [3 "scala"])
;; insert to skills table
  (sql/insert!
    db
    :skills
    nil
    [1 1 "20050401"]
    [1 2 "20060506"]
    [2 1 "20000908"]
    [3 3 "20141020"]))

(defn init-db [db]
  (drop-tables db)
  (create-tables db)
  (load-fixtrues db))

