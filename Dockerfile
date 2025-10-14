# Stage 1: Build the application
FROM eclipse-temurin:21-alpine AS builder

WORKDIR /gatewayservice

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package

# Stage 2: Create minimal runtime image
FROM eclipse-temurin:21-alpine

WORKDIR /gatewayservice

COPY --from=builder /gatewayservice/target/*.jar gatewayservice.jar
# COPY certs ./certs # Uncomment for production deployment

ENV JAVA_OPTS=""

# EXPOSE 8443 # Uncomment for production deployment
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar gatewayservice.jar"]
