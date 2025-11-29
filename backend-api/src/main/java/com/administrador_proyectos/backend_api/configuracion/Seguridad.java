package com.administrador_proyectos.backend_api.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Seguridad {

    // Configuración SEGURA (por defecto - sin perfil 'insecure')
    @Bean
    @Profile("!insecure") // Activo cuando NO está el perfil 'insecure'
    public SecurityFilterChain secureFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    // Configuración INSEGURA (solo para desarrollo)
    @Bean
    @Profile("insecure") // Activo SOLO con perfil 'insecure'
    public SecurityFilterChain insecureFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ¡PERMITE TODO!
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}