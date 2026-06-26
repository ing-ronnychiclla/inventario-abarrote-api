package com.cibertec.inventario.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Permitir preflight (OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // Permitir login y errores sin autenticación
                        .requestMatchers("/api/v1/auth/login", "/error").permitAll()
                        
                        // Categorías: ADMIN completo, ENCARGADO solo lectura (GET). VENDEDOR bloqueado.
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias", "/api/v1/categorias/**").hasAnyRole("ADMIN", "ENCARGADO")
                        .requestMatchers("/api/v1/categorias", "/api/v1/categorias/**").hasRole("ADMIN")

                        // Proveedores: ADMIN completo, ENCARGADO ver, crear y editar, no eliminar (DELETE). VENDEDOR bloqueado.
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/proveedor", "/api/v1/proveedor/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/proveedor", "/api/v1/proveedor/**").hasAnyRole("ADMIN", "ENCARGADO")

                        // Productos: ADMIN completo, ENCARGADO ver, crear y editar, no eliminar (DELETE). VENDEDOR solo consulta (GET).
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/**").hasAnyRole("ADMIN", "ENCARGADO", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos", "/api/v1/productos/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/productos", "/api/v1/productos/**").hasAnyRole("ADMIN", "ENCARGADO")

                        // Inventario / Ventas: ADMIN y VENDEDOR completo. ENCARGADO bloqueado.
                        .requestMatchers("/api/v1/inventario/ventas", "/api/v1/inventario/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Inventario / Ingresos: ADMIN completo (GET, POST, PUT, DELETE), ENCARGADO limitado (GET, POST), VENDEDOR consulta (GET)
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventario/ingresos", "/api/v1/inventario/ingresos/**").hasAnyRole("ADMIN", "ENCARGADO", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/inventario/ingresos", "/api/v1/inventario/ingresos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/inventario/ingresos", "/api/v1/inventario/ingresos/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/inventario/ingresos", "/api/v1/inventario/ingresos/**").hasAnyRole("ADMIN", "ENCARGADO")

                        // Inventario / Movimientos Manuales (Ajustes/Mermas): Solo ADMIN puede realizar ajustes.
                        .requestMatchers(HttpMethod.POST, "/api/v1/inventario/movimientos").hasRole("ADMIN")
                        
                        // Inventario / Kardex (GET general): ADMIN y ENCARGADO. VENDEDOR bloqueado.
                        .requestMatchers(HttpMethod.GET, "/api/v1/inventario").hasAnyRole("ADMIN", "ENCARGADO")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOriginPatterns(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of("*"));
        configuration.setAllowCredentials(true);
        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}