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

(defn valid-update? [old-q new-q]
  (cond
   (string? old-q) (contains-person? new-q old-q)
   (not= (:question old-q) (:question new-q)) false
   :else (and
          (valid-update? (:yes old-q) (:yes new-q))
          (valid-update? (:no old-q) (:no new-q)))))
