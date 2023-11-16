package academy.devdojo.springboot2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //                httpSecurityCsrfConfigurer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    //                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
    )
    .formLogin(Customizer.withDefaults())
    .httpBasic(Customizer.withDefaults());

    return http.build();
  }

  @Bean
  public InMemoryUserDetailsManager userDetailsService() {
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    UserDetails user = User.withUsername("devdojo")
            .password(passwordEncoder.encode("academy"))
            .roles("USER", "ADMIN")
            .build();

    return new InMemoryUserDetailsManager(user);
  }
}
