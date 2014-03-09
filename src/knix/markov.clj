(ns knix.markov)

(defn remove-handles [corpus]
  (vec (doall (map (fn [word] (if (and
                                   (not (= (count word) 1))
                                   (.startsWith word "@"))
                                (subs word 1)
                                word))
                   corpus))))

(defn parse-corpus [s]
  (remove-handles (clojure.string/split s #"\s+")))

(defn generate-tuples [n corpus]
  (partition n 1 corpus))

(defn index-tuple [tuple]
  (let [key (vec (drop-last tuple))
        value (last tuple)]
    [key value]))

(defn index-frequencies [database [key val]]
  (if-let [val-at-key (database key)]
    ;; Prefix is in database
    (let [new-val-at-key (if (contains? val-at-key val)
                           (assoc val-at-key val (inc (val-at-key val)))
                           (assoc val-at-key val 1))]
      (assoc database key new-val-at-key))
    ;; Prefix is not in database
    (assoc database key {val 1})))

(defn build-database [corpus n] (->> corpus
                                     parse-corpus
                                     (generate-tuples (inc n) ,,)
                                     (map index-tuple ,,)
                                     (reduce index-frequencies {} ,,)))


(defn lookup [key database]
  (database key))

(defn select-randomly [val-map]
  (->> val-map
       (map (fn [[k v]] (repeat v k)))
       flatten
       rand-nth))

(defn next-word [database {prefix :prefix}]
  (if-let [value-map (database prefix)]
    (let [new-word (select-randomly value-map)
          new-prefix (conj (subvec prefix 1) new-word)]
      {:word new-word :prefix new-prefix})
    {:word :tail}))

(defn choose-seed [database]
  (key (let [capital-seeds (filter (fn [[k v]]
                                     (Character/isUpperCase (ffirst k))) database)]
         (if (not (empty? capital-seeds))
           (rand-nth (seq capital-seeds))
           (rand-nth (seq database))))))


(defn create-sentence [database]
  (let [seed (choose-seed database)]
    (loop [{word :word :as prefix-map} {:word :head :prefix seed}
           words [(last seed)]]
      (if (or (= :tail word) (< 110 (count (apply str words))))
        words
        (recur (next-word database prefix-map) (conj words word))))))

(defn remove-head [words]
  (filter #(not (= % :head)) words))

(defn generate [corpus n]
  "Retuns a function that will generate an infinite sequence of sentences from the given corpus
   using an order n markov model"
  (let [database (build-database corpus n)
        generation-fn (fn [] (->> (create-sentence database)
                                  remove-head
                                  (interpose " " ,,) 
                                  (apply str ,,)))]
    generation-fn))

;; (defn crossover [corpus-a corpus-b n]
;;   (let [db-a (build-database corpus-a n)
;;         db-b (build-database corpus-b n)]
;;     ))
