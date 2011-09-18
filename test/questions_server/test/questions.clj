(ns questions-server.test.questions
  (:use [questions-server.questions] :reload)
  (:use [clojure.test])
  (:use [midje.sweet]))

(facts "contains-person?"
  (fact "detects a person in a leaf"
    (contains-person? "bill bailey" "bill bailey") => truthy)

  (fact "detects a person in a simple tree"
    (contains-person? (make-question "question?" "bill bailey" "brian cox") "bill bailey") => truthy)

  (fact "detects lack of person in a simple tree"
    (contains-person? (make-question "question?" "bill bailey" "brian cox") "norman lamont") => falsey)

  (fact "detects a person two levels deep in a tree"
    (contains-person? (make-question "question1?"
                                     (make-question "question2?"
                                                    "bill bailey"
                                                    "brian cox")
                                     "norman lamont")
                      "bill bailey")
    => truthy))

(facts "valid-update?"
  (fact "allows replacing an existing leaf with a new question"
    (let
        [old-q (make-question "are you alive?" "Donald Rumsfeld" "George Washington")
         new-q (make-question "are you alive?"
                              (make-question "are you a politician?" "Donald Rumsfeld" "Rich Hickey")
                              "George Washington")]
      (valid-update? old-q new-q) => truthy))


  (fact "doesn't allow replace an existing question with a leaf"
    (let
        [old-q (make-question "are you alive?"
                              (make-question "are you a politician?"
                                             "Donald Rumsfeld"
                                             "Rich Hickey")
                              "George Washington")
         new-q (make-question "are you alive?"
                              "Donald Rumsfeld"
                              (make-question "are you american?"
                                             "George Washington"
                                             "Captain James Cook"))]
      (valid-update? old-q new-q) => falsey))

  (fact "doesn't allow replacing a leaf with a question not containing that leaf"
    (let
        [old-q (make-question "are you alive?"
                              "bill bailey"
                              "don't care")
         new-q (make-question "are you alive?"
                              (make-question "are you hairy?"
                                             "tarzan"
                                             "richard o'brien")
                              "don't care")]
      (valid-update? old-q new-q) => falsey))
)
