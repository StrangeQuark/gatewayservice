FROM eclipse-temurin:21-alpine

WORKDIR /gatewayservice

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

ENV PORT=8080
EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]