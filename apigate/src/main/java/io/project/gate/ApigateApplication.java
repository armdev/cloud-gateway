package io.project.gate;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@Slf4j
@ComponentScan("io.project")
public class ApigateApplication {

    public static void main(String[] args) {
      ///  System.setProperty("myhostname", "127.0.0.1");
        final SpringApplication application = new SpringApplication(ApigateApplication.class);
        application.setBannerMode(Banner.Mode.CONSOLE);
        application.setWebApplicationType(WebApplicationType.REACTIVE);
        application.run(args);
    }

    @Bean
    @CrossOrigin
    public RouteLocator profile(RouteLocatorBuilder builder) {
        log.info("Gate to profile");
        return builder.routes()
                .route("um_route_profile", r -> r
                .path("/profile/**")
                .filters(f -> f.stripPrefix(1).retry(5)
                .circuitBreaker(config -> config.setName("gateCircuitBreaker")
                .setFallbackUri("forward:/failback"))
                .addResponseHeader("Access-Control-Expose-Headers", "scope,client,Origin,Accept-Language,Accept-Encoding")
                .addResponseHeader("TIMESTAMP", LocalDateTime.now().toString())
                )
                .uri("http://localhost:2050/")
                )
                .build();
    }

    @Bean
    @CrossOrigin
    public RouteLocator youtube(RouteLocatorBuilder builder) {
        log.info("Gate to youtube");
        return builder.routes()
                .route("um_route_youtube", r -> r
                .path("/youtube/**")
                .filters(f -> f.stripPrefix(1).retry(5)
                .circuitBreaker(config -> config.setName("gateCircuitBreaker")
                .setFallbackUri("forward:/failback"))
                .addResponseHeader("TIMESTAMP", LocalDateTime.now().toString())
                .addResponseHeader("Access-Control-Expose-Headers", "scope, client,Origin,Accept-Language,Accept-Encoding")
                )
                .uri("https://youtube.com")
                )
                .build();
    }
}
