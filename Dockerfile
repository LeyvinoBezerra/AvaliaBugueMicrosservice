# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN groupadd --system --gid 10001 buggytrip && useradd --system --uid 10001 --gid 10001 buggytrip
RUN mkdir -p /app/logs && chown -R buggytrip:buggytrip /app/logs
COPY --from=build /workspace/target/buggytrip-*.jar /app/app.jar
USER 10001:10001
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"
HEALTHCHECK --interval=15s --timeout=5s --start-period=45s --retries=5 CMD wget -qO- http://127.0.0.1:8080/actuator/health || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
