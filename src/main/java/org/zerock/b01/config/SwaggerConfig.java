package org.zerock.b01.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // /api/** -> RestController
/*    @Bean
    public GroupedOpenApi restApi() {
        return GroupedOpenApi.builder()
                .pathsToMatch("/api/**")
                .group("REST API")
                .build();
    }*/


    // 그 외 나머지 요청 -> Controller
//    @Bean
//    public GroupedOpenApi commonApi() {
//        return GroupedOpenApi.builder()
//                .pathsToMatch("/*/**")
//                .pathsToExclude("/api/**/*")
//                .group("COMMAND API")
//                .build();
//    }
}
