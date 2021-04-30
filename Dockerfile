FROM openjdk:11.0.8-jdk-slim-buster
LABEL MAINTAINER=udomi.ljubimca.mail@gmail.com
RUN apt-get update && apt-get -y upgrade && apt-get -y install wget
RUN groupadd -r app -g 1000 && useradd -u 1000 -r -g app -m -d /web-app -s /sbin/nologin -c "App user" app && \
    chmod 755 /web-app
RUN rm -rf /var/lib/apt/lists/*
WORKDIR /web-app
COPY target/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
USER app
CMD java -Dserver.port=8080 -jar app.jar
