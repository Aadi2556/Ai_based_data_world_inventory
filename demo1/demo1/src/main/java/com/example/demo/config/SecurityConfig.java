package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Frontend pages (Thymeleaf views) ──────────────────────────────
                        .requestMatchers(
                                "/", "/index", "/index.html",
                                "/login", "/login.html",
                                "/manager-login", "/manager-login.html",
                                "/tech-login", "/tech-login.html",
                                "/staff-login", "/staff-login.html",
                                "/register", "/manager-register",
                                "/tech-register", "/staff-register",
                                "/maintenance", "/spareparts", "/materials",
                                "/staffmanagement", "/energy", "/suppliers",
                                "/admin-attendance", "/ai-chat", "/dashboard",
                                "/password-reset", "/password-reset.html"
                        ).permitAll()

                        // ── Static resources (CSS, JS, images, fonts) ─────────────────────
                        .requestMatchers(
                                "/css/**", "/js/**", "/images/**",
                                "/static/**", "/webjars/**",
                                "/*.ico", "/*.png", "/*.svg",
                                "/style.css"
                        ).permitAll()

                        // ── Public auth endpoints (no token required) ──────────────────────
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/password-reset", "/api/password-reset/**").permitAll()
                        .requestMatchers("/api/admin/login").permitAll()
                        .requestMatchers("/api/admin/register").permitAll()
                        .requestMatchers("/api/technician/login").permitAll()
                        .requestMatchers("/api/technician/register").permitAll()
                        .requestMatchers("/api/staff/login").permitAll()
                        .requestMatchers("/api/staff/register").permitAll()
                        .requestMatchers("/api/manager/login").permitAll()
                        .requestMatchers("/api/manager/register").permitAll()

                        // ── Admin-only: full staff & manager management ───────────────────
                        .requestMatchers("/api/admin/staff/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/managers/**").hasRole("ADMIN")

                        // ── Admin & Manager: machine & schedule write operations ──────────
                        .requestMatchers(HttpMethod.POST, "/api/maintenance/schedules/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/maintenance/schedules/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/maintenance/schedules/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/maintenance/machines/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/maintenance/machines/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Maintenance & repairs (Get requests for Staff) ─────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/maintenance/schedules/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/maintenance/machines/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/repairs/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/admin/technicians/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER", "STAFF")

                        // ── Maintenance & repairs ──────────────────────────────────────────
                        .requestMatchers("/api/maintenance/schedules/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER")
                        .requestMatchers("/api/maintenance/machines/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER")
                        .requestMatchers("/api/repairs/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER")
                        .requestMatchers("/api/technician/**").hasAnyRole("ADMIN", "TECHNICIAN", "MANAGER")

                        // ── Energy monitoring ──────────────────────────────────────────────
                        .requestMatchers("/api/energy/**").hasAnyRole("ADMIN", "STAFF", "MANAGER", "TECHNICIAN")

                        // ── Materials management ───────────────────────────────────────────
                        .requestMatchers("/api/materials/**").hasAnyRole("ADMIN", "STAFF", "MANAGER")
                        .requestMatchers("/api/admin/materials/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Spare parts ────────────────────────────────────────────────────
                        .requestMatchers("/api/spare-parts/**").hasAnyRole("ADMIN", "STAFF", "MANAGER")
                        .requestMatchers("/api/spareparts/**").hasAnyRole("ADMIN", "STAFF", "MANAGER", "TECHNICIAN")
                        .requestMatchers("/api/admin/spare-requests/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Suppliers & purchase orders ────────────────────────────────────
                        .requestMatchers("/api/admin/suppliers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/api/suppliers/**").hasAnyRole("ADMIN", "MANAGER")
                        .requestMatchers("/admin/suppliers/**").hasAnyRole("ADMIN", "MANAGER")

                        // ── Staff self-service ─────────────────────────────────────────────
                        .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "STAFF")

                        // ── Manager self-service ───────────────────────────────────────────
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")

                        // ── Attendance ─────────────────────────────────────────────────────
                        .requestMatchers("/api/attendance/technician/**").hasAnyRole("TECHNICIAN", "ADMIN")
                        .requestMatchers("/api/attendance/staff/**").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers("/api/attendance/manager/**").hasRole("MANAGER")
                        .requestMatchers("/api/attendance/admin/**").hasRole("ADMIN")

                        // ── TEMPORARY: permitAll so index page loads for testing ───────────
                        // TODO: change back to .anyRequest().authenticated() after confirming index loads
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}