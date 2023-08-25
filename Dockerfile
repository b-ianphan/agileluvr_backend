### Build Stage ###

### This line switches the user to "gradle" ###
FROM --platform=linux/x86_64 gradle:8.2.1-jdk17-alpine AS build
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradlew ./
RUN ./gradlew dependencies
COPY ./src ./src
RUN ./gradlew clean build -x test

### Package Stage ###
FROM --platform=linux/x86_64 eclipse-temurin:17-jre-alpine
RUN addgroup -S AgileLuvrGroup && adduser -S AgileUser -G AgileLuvrGroup
RUN mkdir -p "/AgileLuvr-server"
RUN chown AgileUser:AgileLuvrGroup "/AgileLuvr-server"
USER AgileUser
WORKDIR "/AgileLuvr-server"
VOLUME "/AgileLuvr-data"
EXPOSE 8080
COPY --from=build /home/gradle/build/libs/agileluvr_backend-1.0-SNAPSHOT.jar /AgileLuvr-server/agileluvr_server.jar
ADD .env /AgileLuvr-server/.env
ENTRYPOINT ["java", "-jar", "/AgileLuvr-server/agileluvr_server.jar"]