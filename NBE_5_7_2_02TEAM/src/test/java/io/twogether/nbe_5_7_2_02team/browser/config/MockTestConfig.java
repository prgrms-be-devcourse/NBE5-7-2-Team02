package io.twogether.nbe_5_7_2_02team.browser.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockTestConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJsonMapper() {
        return builder -> builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }
}
