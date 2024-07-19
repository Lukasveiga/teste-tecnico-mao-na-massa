FROM eclipse-temurin:17-alpine AS builder
WORKDIR /build
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS layer
WORKDIR /layer
ARG JAR_FILE=/build/target/*.jar
COPY --from=builder ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre
WORKDIR /application
COPY --from=layer layer/dependencies/ ./
COPY --from=layer layer/spring-boot-loader/ ./
COPY --from=layer layer/snapshot-dependencies/ ./
COPY --from=layer layer/application/ ./
ENV spring.profiles.active=prod
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

# Health Check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s CMD curl --fail http://localhost:8080/actuator/health || exit 1