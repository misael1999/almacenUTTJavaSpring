package com.flamel.almacenutt;

import com.flamel.almacenutt.auth.filter.JWTAuthenticationFilter;
import com.flamel.almacenutt.auth.filter.JWTAuthorizationFilter;
import com.flamel.almacenutt.auth.services.JWTService;
import com.flamel.almacenutt.models.service.JpaUserDetailsService;
import com.flamel.almacenutt.models.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JpaUserDetailsService userDetailsService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UsuarioService usuarioService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests().antMatchers("/v1/proveedores/todo/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtService, usuarioService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtService))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build) throws Exception {
        build.userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder());
    }



    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:4202", "http://localhost:8888"));
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "HEAD", "OPTIONS", "POST", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin", "X-Requested-With", "Content-Type", "CORELATION_ID", "Authorization", "X-Api-Key", "X-Amz-Date", "accept", "Access-Control-Allow-Methods"));
        configuration.setExposedHeaders(Arrays.asList("Content-Length", "Content-Range", "SessionId"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
