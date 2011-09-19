# Questions server

This is a project I created for the 2011 September London Clojure
Dojo. It is a RESTful web service which serves up clojure data.

# Running

To run, just git clone and use leiningen to start the server:

    lein run

The server should start on port 8080.

# Data format

The data format is a series of nested maps, each representing a
question:

    {:question "Are you a character from Dr Who?"
     :yes      "Dr Who"
     :no       {:question "Are you a famous athlete?"
                :yes      {:question "Are you a sprinter?"
                           :yes "Usain Bolt"
                           :no  "Michael Phelps"}
                :no "Jimmy Carter"}
    }

# Example

To GET the latest twenty questions data, as stringified clojure forms:

    curl localhost:8080/20-questions/latest

To PUT a new version of the data:

    curl -i -X PUT -d '{:question "Are you alive?" :yes "Trevor McDonald" :no {:question "Are you a queen?" :yes "Elizabeth I" :no "Alan Turing"}}' localhost:8080/20-questions/latest

or from a file named `mydata.clj`:

    curl -i -X PUT -d @mydata.clj localhost:8080/20-questions/latest

Note that we use the `-i` option to show the full response. That way,
you can see whether you got a 200 OK, 400 Bad Request or 409 Conflict
response.

# Update rules

The server works to ensure data integrity. It tries to follow two
rules:

 - Once a person is learned, they should never be forgotten. This
   means, for example, that it should never accept a map which doesn't
   contain Trevor McDonald and Elizabeth I, the initial two people in
   the dataset.
 - Once a question is learned, it should never be forgotten, and it
   should be preserved in its original place. This means, for example,
   that the root question should always be "Are you alive?"

If an update is attempted which violates either of these rules, a 409
Conflict HTTP response is issued. If an update is attempted which
isn't even well-formed -- ie invalid Clojure syntax, or not in the
form of a question tree as outlined above -- a 400 Bad Request
response is issued.
