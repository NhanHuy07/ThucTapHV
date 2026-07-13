# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source
COPY src ./src

# Build
RUN mvn package -DskipTests -q

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy jar
COPY --from=builder /app/target/*.jar app.jar

# Permission
RUN mkdir -p logs && chown -R appuser:appgroup /app

USER appuser

# Railway thường dùng PORT env
EXPOSE 8080

# Quan trọng: bind theo PORT của Railway
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]