#!/usr/bin/env bash
cd ..
mvn clean install package
mvn exec:java -D"exec.mainClass"="com.nlp.autoscoring.Main"
