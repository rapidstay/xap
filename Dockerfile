# 빌드 단계 (JDK 21로)
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# 실행 단계
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]