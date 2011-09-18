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

(facts "well-formed?"
  (fact "a string is a well-formed question tree"
    (well-formed? "Bill Bailey") => truthy)
  (fact "a map with precisely the keys :question :yes :no is well-formed"
    (well-formed? {:question "are you bill bailey?" :yes "bb" :no "foo"}) => truthy)
  (fact "a map missing the :no key is not well formed"
    (well-formed? {:question "eh?" :yes "bb"}) => falsey)
  (fact "a map missing the :yes key is not well formed"
    (well-formed? {:question "eh?" :no "bb"}) => falsey)
  (fact "a map missing the :question key is not well formed"
    (well-formed? {:no "eh?" :yes "bb"}) => falsey)
  (fact "a map with a badly-formed submap is not well-formed"
    (well-formed? {:question "foo?" :yes {:question "but no answers?"} :no "foo"}) => falsey)
  (fact "a map with a well-formed submap is well-formed"
    (well-formed? {:question "foo?" :yes {:question "a" :yes "b" :no "c"} :no "foo"}) => truthy,))

(facts "valid-extension?"
  (fact "allows replacing an existing leaf with a new question containing that leaf"
    (let
        [old-q (make-question "are you alive?" "Donald Rumsfeld" "George Washington")
         new-q (make-question "are you alive?"
                              (make-question "are you a politician?" "Donald Rumsfeld" "Rich Hickey")
                              "George Washington")]
      (valid-extension? old-q new-q) => truthy))


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
      (valid-extension? old-q new-q) => falsey))

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
      (valid-extension? old-q new-q) => falsey))
  )
