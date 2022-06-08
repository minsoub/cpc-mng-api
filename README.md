## **프로젝트 소개**
- 고객 보호 센터 관리자 사이트 백엔드

## **기술 스택 소개**

- Java version : JDK 13
- Spring Boot : 2.6.7
- Build : Gradle 7.4.1
- Spring Data MongoDB Reactive
- Spring Boot Webflux
- Spring Boot Actuator
- Spring Boot Security
- Spring Docs Openapi

## **프로젝트 구조**

- API
    - api
    - com.bithumbsystems.cpc.api.{project}
        - domain 
            - controller
            - service
            - model
            - exception
    - core
      - config
      - util
      - model

- Persistence(영속화 영역)
  - mongoDB
    - com.bithumbsystems.persistence.mongodb.{project}
      - model
      - repository
      - service

- Model Mapper : mapstruct

### **서버 구성**

- API 모듈 구성
  - core
      - config - 환경설정
      - model - request,response Object(공통 모델)
        - request
        - response
      - exception - 공통 예외 처리(handler)
      - util
  - project
    - controller - 컨트롤러
    - service - 비지니스 로직
    - model
    - exception

- **여러 Aggregate(도메인 로직)의 조합은 서비스 레이어에서 수행**
- **중복되는 도메인 객체가 생긴다고 하면 도메인 모듈 별로 각자 생성**

### **persistence 영역**

- 영속화 데이터 관리
- persistence-mongodb-{DOMAIN}
    - 도메인 모듈간에는 의존성이 없어야한다.
    - 도메인 모듈이 비대해지면 별도의 서비스로 분리한다.
  - **도메인 로직은 서비스에 있어선 안됨**
  - 도메인 객체, 레파지토리는 1:1
      - model
      - repository
      - service
- 추후 다른 프로젝트에서 재사용가능(분리해서 내부 라이브러리로도 사용가능)
- 추가로 persistence(data) 가 생길 경우, 별도 모듈 생성 후 사용 서비스에 주입

## Running the tests
- Swagger 참고
    - management: http://127.0.0.1:8081/v1/swagger-ui.html
- Docker
    - ./docker/docker-compose.yml
    - ```shell
      docker-compose -f ./docker/docker-compose.yml up -d
      ```

- local에서 AWS EC2에러시 
  - Vm Option에 추가 후 실행/
  - Dcom.amazonaws.sdk.disableEc2Metadata=true

## Build
```shell
/deploy/build.sh
```

## BootRun
```shell
 ./gradlew -b api/build.gradle bootRun
```

## history
- 0.0.1 초안 init

### License