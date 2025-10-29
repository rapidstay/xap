# ✅ 1단계: 빌드 단계 (Gradle로 JAR 생성)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# ✅ 2단계: 실행 단계 (JAR만 복사)
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
