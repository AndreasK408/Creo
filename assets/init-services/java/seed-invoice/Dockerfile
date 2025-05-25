FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /usr/src/seed-invoice

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /opt/seed-invoice

COPY --from=builder /usr/src/seed-invoice/target/seed-invoice-1.0.0.jar ./seed-invoice.jar

ENTRYPOINT ["java", "-jar", "seed-invoice.jar"]
