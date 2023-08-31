package com.banking.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@EnableEurekaClient
@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    //    We can do routing by directly configuring these as properties in application.properties file or .yml file
    @Bean
    public RouteLocator bankServiceRoutes(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(p -> p
                        .path("/banking/account-service/**")   //getting the path from the coming request
                        .filters(f -> f.rewritePath("/banking/account-service/(?<segment>.*)", "/account-service/${segment}")  //removing "banking" part from the path
                                .addResponseHeader("X-Response-Time", new Date().toString())    //added a separate response header to represent the response time
                        )
                        .uri("lb://ACCOUNT-SERVICE")    //directing to the service registered in discovery-server
                )
                .route(p -> p.path("/banking/loan-service/**")
                        .filters(f -> f.rewritePath("/banking/loan-service/(?<segment>.*)", "/loan-service/${segment}")
                                .addResponseHeader("X-RESPONSE-TIME", new Date().toString())
                        )
                        .uri("lb://LOAN-SERVICE")
                )
                .route(p -> p.path("/banking/card-service/**")
                        .filters(f -> f.rewritePath("/banking/card-service/(?<segment>.*)", "/card-service/${segment}")
                                .addResponseHeader("X-RESPONSE-TIME", new Date().toString())
                        )
                        .uri("lb://CARD-SERVICE")
                )
                .route(p -> p.path("/banking/customer-service/**")
                        .filters(f -> f.rewritePath("/banking/customer-service/(?<segment>.*)", "/customer-service/${segment}")
                                .addResponseHeader("X-RESPONSE-TIME", new Date().toString())
                        )
                        .uri("lb://CUSTOMER-SERVICE")
                )
                .build();
    }
}
