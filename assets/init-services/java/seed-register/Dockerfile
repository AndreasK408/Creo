FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /usr/src/seed-register

COPY pom.xml .


COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/seed-register

COPY --from=builder /usr/src/seed-register/target/seed-register-1.0.0.jar ./seed-register.jar

ENTRYPOINT ["java", "-jar", "seed-register.jar"]