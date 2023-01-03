# Spring ApiGateway Service

## 1. 필터

### 1.1. 라우팅 필터
- 아래와 같은 방식으로 자바 소스를 통해 라우팅 필터 설정이 가능하다.
```java
@Configuration
public class FilterConfig
{
  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder)
  {
    return builder.routes()
      .route(r -> r.path("/first-service/**")
        .filters(f -> f.addRequestHeader("first_request", "first-request-header")
                       .addResponseHeader("first-response", "first-response-header"))
        .uri("http://localhost:8081"))
      .route(r -> r.path("/second-service/**")
        .filters(f -> f.addRequestHeader("second_request", "second-request-header")
                       .addResponseHeader("second-response", "second-response-header"))
        .uri("http://localhost:8082"))
      .build();
  }
}
```
- 자바가 아닌 properties, yml을 통해 라우팅을 설정하고 싶다면 아래와 같은 설정을 통해 라우팅을 설정할 수 있다.
```yaml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri : http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
            - AddRequestHeader=first_request, first-request-header2
            - AddResponseHeader=first_response, first-response-header2
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=second_request, second-request-header2
            - AddResponseHeader=second_response, second-response-header2
```


### 1.2. 커스텀 필터
- 각 서비스 개별로 적용할 수 있는 커스텀 필터
- 아래와 같이 AbstractGatewayFilterFactory를 상속받는 클래스를 하나 생성하고, properties, yml 설정 파일에 기재하여 동작시킨다.
```java
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config>
{
  public CustomFilter()
  {
    super(CustomFilter.Config.class);
  }

  @Override
  public GatewayFilter apply(CustomFilter.Config config)
  {
    return (exchange, chain) -> {
      // Custom pre filter
      ServerHttpRequest request = exchange.getRequest();
      ServerHttpResponse response = exchange.getResponse();
      
      // do request something

      // Custom post filter
      return chain.filter(exchange)
        .then(Mono.fromRunnable(() -> {
          // do response something
        }));
    };
  }

  public static class Config
  {
    // put config properties
  }
}
```
```yaml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
          filters:
            - CustomFilter
```

---

### 1.3. 글로벌 필터