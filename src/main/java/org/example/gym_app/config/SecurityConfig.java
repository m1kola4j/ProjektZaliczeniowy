package org.example.gym_app.config;

import lombok.RequiredArgsConstructor;
import org.example.gym_app.model.User;
import org.example.gym_app.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // publiczne rzeczy
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/h2-console/**",
                                "/login",
                                "/register",
                                "/",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()
                        // tylko ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // reszta: każdy zalogowany (USER lub ADMIN)
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        // pozwalamy na <iframe> dla H2
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // userzy ładowani z bazy (tabela users)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + username));

            UserDetails details = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword()) // JUŻ zakodowane w bazie
                    .roles(user.getRole())        // np. "USER" albo "ADMIN"
                    .build();

            return details;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
