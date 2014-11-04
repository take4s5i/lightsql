(ns light-sql.core
  (:require [clojure.java.jdbc :as sql]
            [clojure.string :as cst]
            [clojure.data.json :as json]))

(defn parametarize [default s] 
  (fn [params]
    (let [sub (fn [s [p n]] (cst/replace s (str "${" n "}") (str p)))
          r (reduce sub s (map vector params (iterate inc 1)))]
      (cst/replace r #"\$\{[0-9]+\}" default))))


(defn query [db sqls params]
  (let [q (fn [fsql] (mapcat #(sql/query db [(fsql %)]) params))
        psqls (map (partial parametarize "null") sqls)]
    (map q psqls)))

(defn topological-sort 
  ([topos]
   (let [nodes (keys topos)
         v (transient [])
         visited (atom #{})]
     (doseq [n nodes] (topological-sort n topos visited v))
     (persistent! v)))
  ([node topos visited v]
    (when-not (@visited node)
      (do
        (swap! visited conj node)
        (doseq [n (topos node)]
          (topological-sort n topos visited v))
        (conj! v node)))))


(defmulti convert (fn [_ t] t))
(defmethod convert :json [m _] (json/write-str m))
(defmethod convert :default [m _] (prn-str m))

(defn create-index [table fkey]
  (reduce (fn [a [k v]] 
            (if (a k)
                (assoc a k (conj (a k) v))
                (assoc a k [v])))
          {}
          (map vector (map fkey table)
                      (iterate inc 0))))

(defn join
  ([fkey tbl1 tbl2 prop] (join fkey fkey tbl1 tbl2))
  ([fkey1 fkey2 tbl1 tbl2 prop]
   (let [idx (create-index tbl2 fkey2)
         join-map (fn [m] (assoc m 
                                 prop 
                                 (when-let [x (idx (fkey1 m))] 
                                   (vec (map tbl2 x)))))]
     (map join-map tbl1)))
  ([tbl-map join-map]
   nil))




