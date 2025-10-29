# 🏨 RapidStay & XAP Integrated Development Guide

> Expedia 기반 호텔 검색/상세 시스템  
> Front: Firebase (rapidstay)  
> Back: Spring Boot (xap, Render 배포)

---

## 1️⃣ 전체 아키텍처 개요

### 📦 XAP (Spring Boot API)
- **역할**: 호텔 검색, 상세 조회, 캐싱, CORS 관리
- **배포 환경**: Render (Dockerfile 기반)
- **언어/버전**: Java 21, Gradle 8.5
- **주요 포트**: 8080

### 💻 RapidStay (Firebase Hosting)
- **역할**: 사용자 검색 UI / 호텔 리스트 / 상세 페이지
- **URL**: https://rapidstay-c7f8e.web.app
- **빌드 없이 정적 HTML 배포**

---

## 2️⃣ 백엔드 구조 (XAP)

### 📁 주요 파일
| 파일명 | 역할 |
|--------|------|
| `HotelResponse.java` | 호텔 상세 DTO |
| `RoomResponse.java` | 객실 세부 DTO |
| `RedisConfig.java` | 환경별 Redis 연결 관리 |
| `application.yml` | local / prod 프로필 분리 |
| `GlobalExceptionHandler.java` | API 예외 로깅 및 응답 |

---

## 3️⃣ Redis 설정

```java
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    @Profile("local") // 로컬용
    public RedisConnectionFactory localRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    @Profile("prod") // Render용
    public RedisConnectionFactory prodRedisConnectionFactory() {
        return new LettuceConnectionFactory("red-d40ub0k9c44c73cdgu60", 6379);
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                            new GenericJackson2JsonRedisSerializer()
                        )
                );
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    @Bean
    public SimpleKeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}

prod 프로필: Render 내부 Redis 인스턴스 (red-d40ub0k9c44c73cdgu60)

local 프로필: localhost:6379

TTL: 30분

직렬화: GenericJackson2JsonRedisSerializer

4️⃣ CORS 설정
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("https://rapidstay-c7f8e.web.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowCredentials(true);
}

5️⃣ Dockerfile (Render 배포용)
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

6️⃣ Render 배포 절차

Git push 후 자동 트리거

git add .
git commit -m "deploy update"
git push origin main


Render 대시보드 → Deploy logs에서 상태 확인

정상시: “Live” 표시

에러 발생 시 logs 탭에서 Redis 연결 및 포트 확인

7️⃣ 프론트엔드 구조 (Firebase)
📄 index.html

도시, 체크인/체크아웃, 성인/아동 입력

/api/hotels/search 호출

const res = await fetch(`https://xap-h2xh.onrender.com/api/hotels/search?city=${city}&checkIn=${checkin}&checkOut=${checkout}&adults=${adults}&children=${children}`);

📄 detail.html

URL 파라미터에서 id 포함해 호출

검색 시 받은 조건(city, checkIn, checkOut, adults, children)도 함께 전달

const res = await fetch(`https://xap-h2xh.onrender.com/api/hotels/detail?hotelId=${id}&city=${city}&checkIn=${checkin}&checkOut=${checkout}&adults=${adults}&children=${children}`);


room.images, room.amenities, room.description만 표시

가격 관련 정보는 제외

8️⃣ 공통 규칙
구분	항목	값
Java	21	
Gradle	8.5	
Redis	Render Free Tier	
Maps API	Google Maps	
Hosting	Firebase	
API Host	https://xap-h2xh.onrender.com
	
프론트 Host	https://rapidstay-c7f8e.web.app
	
9️⃣ 점검 포인트

 RedisConfig에 prod 주소 정확히 반영

 Render 환경변수에 SPRING_PROFILES_ACTIVE=prod

 API 호출 도메인 일치 (xap-h2xh.onrender.com)

 Firebase Hosting 업데이트 후 캐시 무효화

 Google Maps API Key 추가

10️⃣ 향후 확장 계획

✅ 성인/아동 조건 유지된 상세 조회

✅ Redis TTL 동적 조정

✅ 호텔 상세 내 객실 팝업 UX 개선

🧩 다국어 UI 연동

🧩 AWS S3 이미지 업로드 기능

🧩 예약 API 연동 (차후 Expedia API 교체)

📘 이 문서는 RapidStay + XAP 통합 시스템의 기준 버전입니다.
새 프로젝트 생성 시 이 파일만 복사하면 전체 개발환경 및 API 구조가 복원됩니다.


cd /Users/kim-inho/Documents/xap
git add .
git commit -m "update fetch URLs for Render deployment"
git push origin main

Render 서버 배포.
https://dashboard.render.com/web/srv-d40t1lruibrs73col160/deploys/dep-d40ttogdl3ps73eljung
xap 서비스 → Deploys 탭 → Manual Deploy → Clear build cache & Deploy 클릭.
