# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/uno-cli.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--bots", "3", "--games", "1"]
