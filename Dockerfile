# Defining a multi-stage image
# Stage 1: build with JDK 17 in OS Ubuntu 22.04 LTS (Jammy Jellyfish)
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /app

# Copy only maven wrapper and pom to leverage Docker cache for dependencies
COPY mvnw pom.xml ./
# Copy the directory .mvn to workdir (needed to use maven wrapper)
COPY .mvn .mvn
# Give mvnw execution permission
RUN chmod +x mvnw

# Download pom dependencies needed to build (no compile)
RUN ./mvnw -B dependency:go-offline

#Copy source code and build( tests are skipped here because they should run in CI before building image)
COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# Stage 2: runtime with JRE 17 (Jammy Jellyfish)
FROM eclipse-temurin:17-jre-jammy

# Create an unprivileged user for better security (TO DO)
# RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app
# Copy only the built jar from the builder stage and rename it to app.jar
COPY --from=builder /app/target/store-1.0.0.jar app.jar

# Expose port (informative)
EXPOSE 8080
# Healthcheck (optional TO DO)
#HEALTHCHECK --interval=30s --timeout=5s --start-period=30s \
#  CMD curl -f http://localhost:8080/actuator/health || exit 1
# Run as non-root
#USER appuser

# Execute the executable jar
ENTRYPOINT ["java", "-jar", "app.jar"]