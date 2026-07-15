package com.entrepot.gestion.config;

import com.entrepot.gestion.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/utilisateurs/nouveau", "/utilisateurs/enregistrer").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/profil/**").authenticated()
                .requestMatchers("/", "/mouvements/**").authenticated()
                .requestMatchers("/accueil", "/choose-type_zones", "/type-zone/**", "/recherche", "/faire-recherche").authenticated()
                .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "GESTIONNAIRE", "COMPTABLE")
                .requestMatchers("/chauffeurs/**", "/vehicules/**", "/livraisons/**", "/missions/**", "/maintenances/**")
                    .hasAnyRole("ADMIN", "GESTIONNAIRE", "RESPONSABLE_LOGISTIQUE")
                .requestMatchers("/clients/**", "/utilisateurs/**").hasAnyRole("ADMIN", "GESTIONNAIRE", "COMPTABLE")
                .requestMatchers("/contrats/**").hasAnyRole("ADMIN", "GESTIONNAIRE", "COMPTABLE")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/mouvements/tableau-de-bord", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
