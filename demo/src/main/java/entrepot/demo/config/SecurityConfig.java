package entrepot.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import entrepot.demo.service.AuthDetailsService;

@Configuration
public class SecurityConfig {

    private final AuthDetailsService authDetailsService;

    public SecurityConfig(AuthDetailsService authDetailsService) {
        this.authDetailsService = authDetailsService;
    }

    /*
     * Permet de comparer le mot de passe entré
     * avec le hash BCrypt en base
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    /*
     * Connexion entre Spring Security
     * et notre table utilisateurs
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(authDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                /*
                 * Gestion des accès
                 */
                .authorizeHttpRequests(auth -> auth

                        // pages publiques
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**")
                        .permitAll()

                        /*
                         * Gestion utilisateurs
                         * seulement ADMIN
                         */
                        .requestMatchers(
                                "/utilisateurs/**")
                        .hasRole("ADMIN")

                        /*
                         * Gestion clients
                         */
                        .requestMatchers(
                                "/clients/**")
                        .hasRole("ADMIN")

                        /*
                         * Tout le reste nécessite une connexion
                         */
                        .anyRequest()
                        .authenticated()

                )

                /*
                 * Formulaire de connexion
                 */
                .formLogin(login -> login

                        .loginPage("/login")

                        /*
                         * URL appelée par le formulaire
                         */
                        .loginProcessingUrl("/login")

                        /*
                         * après succès
                         */
                        .successHandler(
                                (request, response, authentication) -> {

                                    boolean admin = authentication
                                            .getAuthorities()
                                            .stream()
                                            .anyMatch(
                                                    a -> a.getAuthority()
                                                            .equals("ROLE_ADMIN"));

                                    if (admin) {

                                        response.sendRedirect(
                                                "/clients");

                                    } else {

                                        response.sendRedirect(
                                                "/accueil");
                                    }

                                })

                        .failureUrl(
                                "/login?error=true")

                        .permitAll())

                /*
                 * Déconnexion
                 */
                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl(
                                "/login?logout=true")

                        .permitAll());

        return http.build();
    }

}