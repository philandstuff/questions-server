(ns questions-server.core
  (:use compojure.core, ring.adapter.jetty)
  (:use clojure.java.io)
  (:use questions-server.questions)
  (:require [compojure.route :as route]))

(def data (ref (make-question  "Are you alive?"
                               "Trevor McDonald"
                               "Elizabeth I")))

(defn conflict-response [existing-data]
  {:status 409
   :body (str "your update is not a valid extension of the existing data: "
              existing-data)})

(defn success-response [new-data]
  {:status 200
   :body (str "saved your request of " new-data)})

(defn bad-request-response []
  {:status 400
   :body (str "your request doesn't appear to be a valid question tree. Check that it is valid clojure syntax, and a valid question tree data structure.")})

(defn read-body [entity-stream]
  (try (let [parsed-entity (read (java.io.PushbackReader. (reader entity-stream)))]
         (when (well-formed? parsed-entity)
           parsed-entity))
       (catch Exception e nil) ; EOF while reading
       ))

(defroutes main-routes
  (GET "/" [] "<h1>Hello World</h1>")
  (GET "/20-questions/latest" [] (str @data))
  (PUT "/20-questions/latest" {entity-stream :body}
       (if-let [body (read-body entity-stream)]
         (dosync 
          (ensure data)
          (if (not (valid-extension? @data body))
            (conflict-response @data)
            (do
              (ref-set data body)
              (success-response body))))
         (bad-request-response)))
  (route/not-found "<h1>Page not found</h1>"))

(defn -main [] (run-jetty (var main-routes) {:port 8080 :join? false}))
