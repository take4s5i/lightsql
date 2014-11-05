(ns lightsql.core
  (:gen-class)
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as cst]
            [clojure.data.json :as json]
            [clojure.java.io :as io])
  (:import (java.io File)))

(defmulti convert (fn [_ t] t))
(defmethod convert :json [m _] (json/write-str m :escape-unicode false))
(defmethod convert :default [m _] (prn-str m))

(extend java.lang.Object json/JSONWriter {:-write (fn [x out] (.print out (str \" x \")))})

(defn query [conn sql & params]
  (let [db {:connection-uri conn }
        result (sql/query db (apply vector sql params))
        outstr (convert (vec result) :json)]
    outstr))

(defn -main [conn sql-file & params]
  (println (apply query conn (slurp sql-file) params)))
  

