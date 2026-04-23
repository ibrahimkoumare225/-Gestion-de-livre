FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY *.properties .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build application
RUN ./gradlew build -x test --no-daemon

# Extract jar from build
RUN mkdir -p docker/main && cd docker/main && jar -xf ../../build/libs/gestion_de_livre-*.jar

# Extract jar layers
WORKDIR /app/docker/main
RUN cp -r BOOT-INF/lib /app/docker/lib
RUN cp -r BOOT-INF/classes /app/docker/classes
RUN cp -r META-INF /app/docker/

# Final image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Copy layers from builder
COPY --from=0 /app/docker/lib lib
COPY --from=0 /app/docker/classes classes
COPY --from=0 /app/docker/META-INF META-INF

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "classes:lib/*", "fr.koumare.gestion_de_livre.GestionDeLivreApplicationKt"]
