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

(defroutes main-routes
  (GET "/" [] "<h1>Hello World</h1>")
  (GET "/20-questions/latest" [] (str "updated! " @data))
  (PUT "/20-questions/latest" req
       (let [body (read (java.io.PushbackReader. (reader (:body req))))]
         (dosync 
          (ensure data)
          (if (not (valid-update? @data body))
            (conflict-response @data)
            (do
              (ref-set data body)
              (str "saved your request of " body))))))
  (route/not-found "<h1>Page not found</h1>"))

(defn -main [] (run-jetty (var main-routes) {:port 8080 :join? false}))
