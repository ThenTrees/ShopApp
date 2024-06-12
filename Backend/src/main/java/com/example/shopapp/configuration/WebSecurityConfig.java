package com.example.shopapp.configuration;

import static org.springframework.http.HttpMethod.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.example.shopapp.filters.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@Configuration
// @EnableMethodSecurity
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
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
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix))
                            .permitAll()
                            .requestMatchers(GET, String.format("%s/roles**", apiPrefix))
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

        //        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
        //            @Override
        //            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
        //                CorsConfiguration configuration = new CorsConfiguration();
        //                configuration.setAllowedOrigins(List.of("*"));
        //                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE",
        // "OPTIONS"));
        //                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type",
        // "x-auth-token"));
        //                configuration.setExposedHeaders(List.of("x-auth-token"));
        //                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //                source.registerCorsConfiguration("/**", configuration);
        //                httpSecurityCorsConfigurer.configurationSource(source);
        //            }
        //        });

        return http.build();
    }
}
