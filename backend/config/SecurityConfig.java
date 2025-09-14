package com.example._th_project.config;

import com.example._th_project.jwt.JwtAuthenticationFilter;
import com.example._th_project.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtUtil, redisTemplate);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/signup", "/users/login", "/users/DuplicateTest", "/", "/index.html", "/static/**", "/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/**", "/users/**/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/users/**", "/users/**/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/users/**", "/users/**/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/users/**", "/users/**/**").hasRole("USER")
                        .requestMatchers("/admin/login", "/admin/signup",
                                "/admin/check-id", "/admin/reissue",  "/register", "users/login", "/users/DuplicateTest", "users/signup").permitAll()

                        .requestMatchers(HttpMethod.GET,  "/admin/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.HEAD, "/admin/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.OPTIONS,"/admin/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable());

        return http.build();
    }

    /** CORS */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("/**"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
