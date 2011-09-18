*Questions server*

This is a project I created for the 2011 September London Clojure
Dojo. It is a RESTful web service which serves up clojure data.

*Example*

To GET the latest twenty questions data, as stringified clojure forms:

    curl localhost:8080/20-questions/latest

To PUT a new version of the data:

    curl -i -X PUT -d 'foo' localhost:8080/20-questions/latest

Note that we use the `-i` option to show the full response. That way,
you can see that if your data is not a valid extension of the existing
data, you get a 409 Conflict response.
