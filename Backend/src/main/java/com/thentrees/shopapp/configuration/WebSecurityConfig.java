package com.thentrees.shopapp.configuration;

import static org.springframework.http.HttpMethod.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.thentrees.shopapp.filters.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
// @EnableMethodSecurity
@EnableWebMvc
@RequiredArgsConstructor
@EnableWebSecurity()
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers(
                                    String.format("%s/authentications/register", apiPrefix),
                                    String.format("%s/authentications/login", apiPrefix), // healthcheck
                                    String.format("%s/authentications/login-google", apiPrefix), // healthcheck
                                    String.format("%s/healthcheck/**", apiPrefix),
                                    // swagger
                                    // "/v3/api-docs",
                                    // "/v3/api-docs/**",
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/**",
                                    "/swagger-ui/index.html")
                            .permitAll()
                            .requestMatchers(GET, String.format("/"))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/roles**", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/comments**", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/categories/**", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/products/**", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/products/images/*", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/orders/**", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/order_details/**", apiPrefix))
                            .permitAll()
                            .anyRequest()

                            .authenticated();
                });
        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        return http.build();
    }
}
