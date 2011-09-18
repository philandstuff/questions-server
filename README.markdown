# Questions server

This is a project I created for the 2011 September London Clojure
Dojo. It is a RESTful web service which serves up clojure data.

# Running

To run, just git clone and use leiningen to start the server:

    lein run

The server should start on port 8080

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

    curl -i -X PUT -d 'foo' localhost:8080/20-questions/latest

or from a file named `mydata.clj`:

    curl -i -X PUT -d @mydata.clj localhost:8080/20-questions/latest

Note that we use the `-i` option to show the full response. That way,
you can see that if your data is not a valid extension of the existing
data, you get a 409 Conflict response.
