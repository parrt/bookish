#!/bin/bash

echo "java -jar /Users/parrt/github/bookish/target/bookish-1.0-SNAPSHOT.jar -target html-article -o /tmp/gradient-boosting $1"
java -jar /Users/parrt/github/bookish/target/bookish-1.0-SNAPSHOT.jar -target html-article -o /tmp/gradient-boosting $1

#java -jar /Users/parrt/github/bookish/target/bookish-1.0-SNAPSHOT.jar -target html -o /tmp/mlbook /Users/parrt/github/mlbook-private/content/book.json
