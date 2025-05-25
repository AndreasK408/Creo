FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /usr/src/seed-login

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/seed-login

COPY --from=builder /usr/src/seed-login/target/seed-login-1.0.0.jar ./seed-login.jar

ENTRYPOINT ["java", "-jar", "seed-login.jar"]
