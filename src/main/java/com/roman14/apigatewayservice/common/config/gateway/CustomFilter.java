package com.roman14.apigatewayservice.common.config.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<com.roman14.apigatewayservice.common.config.gateway.CustomFilter.Config>
{
  public CustomFilter()
  {
    super(com.roman14.apigatewayservice.common.config.gateway.CustomFilter.Config.class);
  }

  @Override
  public GatewayFilter apply(com.roman14.apigatewayservice.common.config.gateway.CustomFilter.Config config)
  {
    return (exchange, chain) -> {
      // Custom pre filter
      ServerHttpRequest request = exchange.getRequest();
      ServerHttpResponse response = exchange.getResponse();

      log.info("Custom pre filter : request id -> {}", request.getId());

      // Custom post filter
      return chain.filter(exchange)
        .then(Mono.fromRunnable(() -> {
          log.info("Custom post filter : response code -> {}", response.getStatusCode());
        }));
    };
  }

  public static class Config
  {
    // 설정 정보를 기입
  }
}
