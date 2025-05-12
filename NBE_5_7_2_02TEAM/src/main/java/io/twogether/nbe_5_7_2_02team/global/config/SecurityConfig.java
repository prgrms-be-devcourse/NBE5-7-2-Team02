package io.twogether.nbe_5_7_2_02team.global.config;

import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtAuthenticationFilter;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.OAuth2SuccessHandler;
import io.twogether.nbe_5_7_2_02team.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuthService oAuthService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .httpBasic(httpB -> httpB.disable())
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2Login(oauth -> {
                oauth.successHandler(oAuth2SuccessHandler);
            })
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers("/api/**")
                .hasAnyAuthority("ADMIN", "MEMBER")
                .requestMatchers("/api/admin/**")
                .hasAnyAuthority("ADMIN")
                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }


}
