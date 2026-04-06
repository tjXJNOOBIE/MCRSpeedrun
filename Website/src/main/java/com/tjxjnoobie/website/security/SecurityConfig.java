package com.tjxjnoobie.website.security;

import com.tjxjnoobie.website.config.StoreApplicationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain websiteSecurityFilterChain(HttpSecurity http,
                                                          UserDetailsService userDetailsService,
                                                          OAuth2UserService<?, ?> oauth2UserService) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/support",
                                "/checkout/**",
                                "/account/**",
                                "/api/v1/store/**",
                                "/api/v1/checkout/**",
                                "/api/v1/payments/create-intent",
                                "/api/v1/payments/webhook/stripe",
                                "/api/v1/players/**",
                                "/api/v1/account/**",
                                "/api/v1/integration/**",
                                "/admin/login",
                                "/login/**",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/actuator/health",
                                "/favicon.ico"
                        ).permitAll()
                        .requestMatchers("/admin/**", "/api/v1/admin/**").hasAnyRole("ADMIN", "STORE_MANAGER", "SUPPORT_AGENT", "AUDITOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin", true)
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/admin/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(castOauthService(oauth2UserService)))
                        .defaultSuccessUrl("/admin", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login?logout")
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                "/api/v1/account/**",
                                "/api/v1/checkout/**",
                                "/api/v1/integration/**",
                                "/api/v1/payments/create-intent",
                                "/api/v1/payments/webhook/stripe",
                                "/api/v1/support/tickets"
                        )
                )
                .rememberMe(Customizer.withDefaults());
        return http.build();
    }

    @SuppressWarnings("unchecked")
    private OAuth2UserService<org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest, OAuth2User> castOauthService(
            OAuth2UserService<?, ?> service
    ) {
        return (OAuth2UserService<org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest, OAuth2User>) service;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(StoreApplicationProperties properties) {
        return new StoreClientRegistrationRepository(properties);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
