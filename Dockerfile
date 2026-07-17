FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --create-home --uid 10001 fincorex

COPY --from=build --chown=fincorex:fincorex /app/target/fincorex-0.0.1-SNAPSHOT.jar app.jar

USER fincorex

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
