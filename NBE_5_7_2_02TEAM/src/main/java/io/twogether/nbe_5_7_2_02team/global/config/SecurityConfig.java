package io.twogether.nbe_5_7_2_02team.global.config;

import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtAuthenticationFilter;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.OAuth2FailureHandler;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.OAuth2SuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.httpBasic(httpB -> httpB.disable())
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(
                        oauth -> {
                            oauth.successHandler(oAuth2SuccessHandler);
                            oauth.failureHandler(oAuth2FailureHandler);
                        })
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(CorsUtils::isPreFlightRequest)
                                        .permitAll()
                                        .requestMatchers("/api/chatroom/entered")
                                        .authenticated()
                                        .requestMatchers(
                                                "/ws/chatroom/**",
                                                "/api/chatroom/**",
                                                "/api/tags/**",
                                                "/api/oauth2/**",
                                                "/api/tags",
                                                "/api/token/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/posts")
                                        .permitAll()
                                        .requestMatchers("/api/**")
                                        .hasAnyAuthority("MEMBER")
                                        .anyRequest()
                                        .permitAll())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        log.info("CORS 설정 동작: {}", config.getAllowedOrigins());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
