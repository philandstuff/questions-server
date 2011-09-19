(ns questions-server.test.core
  (:use [questions-server.core] :reload)
  (:use questions-server.questions)
  (:use clojure.test)
  (:use midje.sweet))

(defn get-request [uri]
  {:protocol :http
   :request-method :get
   :uri uri})

(defn string-to-stream [string]
  (java.io.ByteArrayInputStream.
   (.getBytes string))) ; FIXME: should really specify encoding here

(defn put-request [uri body]
  {:protocol :http
   :request-method :put
   :uri uri
   :body (string-to-stream body)})

(fact "/ is a hello world response"
  (:body (main-routes (get-request "/"))) => (contains "Hello World"))

(fact "20 questions latest returns current state of data"
  (:body (main-routes (get-request "/20-questions/latest")))
  => (str @data))



(fact "Can update tree"
  (let [my-tree (make-question "Are you a newsreader?"
                               "Trevor McDonald"
                               "Elizabeth I")]
    (main-routes (put-request "/20-questions/latest"
                              (str my-tree))))
  => (contains {:status 200})
  (against-background
    (before :facts
            (dosync
             (ref-set data "Trevor McDonald")))))

(fact "Ill-formed clojure results in bad request"
  (main-routes (put-request "/20-questions/latest"
                            "{\"look ma, no closing brace!\""))
  => (contains {:status 400})
  (against-background
    (before :facts
            (dosync
             (ref-set data "Trevor McDonald")))))

(fact "Well-formed tree which isn't valid extension results in conflict"
    (let [my-tree (make-question "Are you a newsreader?"
                               "John Snow"
                               "Elizabeth I")]
    (main-routes (put-request "/20-questions/latest"
                              (str my-tree))))
  => (contains {:status 409})
  (against-background
    (before :facts
            (dosync
             (ref-set data "Trevor McDonald")))))
