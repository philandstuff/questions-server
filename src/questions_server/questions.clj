(ns questions-server.questions)

(defn make-question [q yes-q no-q] {:question q :yes yes-q :no no-q})

(defn contains-person? [question-tree person]
  (if (string? question-tree)
    (= person question-tree)
    (or
     (contains-person? (:yes question-tree)
                       person)
     (contains-person? (:no question-tree)
                       person)))) 

(defn well-formed? [question-tree]
  (cond
   (string? question-tree) true
   (map? question-tree) (and
                         (every? (partial contains? question-tree) [:question :yes :no])
                         (well-formed? (:yes question-tree))
                         (well-formed? (:no question-tree)))
   :else false))

(defn valid-extension? [old-q new-q]
  (cond
   (string? old-q) (contains-person? new-q old-q)
   (not= (:question old-q) (:question new-q)) false
   :else (and
          (valid-extension? (:yes old-q) (:yes new-q))
          (valid-extension? (:no old-q) (:no new-q)))))

(defn valid-update? [old-tree new-tree]
  (and
   (well-formed? new-tree)
   (valid-extension? old-tree new-tree)))
