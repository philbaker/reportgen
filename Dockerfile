# syntax = docker/dockerfile:1.2
FROM clojure:openjdk-11 AS build

WORKDIR /
COPY . /

# deps.edn uberjar
RUN clj -Sforce -T:build all
# leiningen uberjar. Uncomment if you are using leiningen and deleted your deps.edn file
# RUN lein uberjar

FROM adoptopenjdk/openjdk11:alpine-slim

COPY --from=build /target/uberjar/reportgen.jar /reportgen/reportgen.jar

EXPOSE $PORT
EXPOSE $REPL_PORT

ENTRYPOINT exec java $JAVA_OPTS -jar /reportgen/reportgen.jar

