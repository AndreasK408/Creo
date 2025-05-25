FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /usr/src/seed-user

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/seed-user

COPY --from=builder /usr/src/seed-user/target/seed-user-1.0.0.jar ./seed-user.jar

ENTRYPOINT ["java", "-jar", "seed-user.jar"]
