#!/bin/bash

I="/Users/parrt/github/bookish/examples/matrix-calculus"
O="/tmp/matrix-calculus"

java -jar /Users/parrt/github/bookish/target/bookish-1.0-SNAPSHOT.jar -target html -o $O $I/article.xml
