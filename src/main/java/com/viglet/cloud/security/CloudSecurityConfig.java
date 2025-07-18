package com.viglet.cloud.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Profile("production")
@EnableMethodSecurity(securedEnabled = true)
@ComponentScan(basePackageClasses = CloudCustomUserDetailsService.class)
public class CloudSecurityConfig {
    public static final String ERROR_PATH = "/error/**";
    @Autowired
    private UserDetailsService userDetailsService;
    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri:''}")
    private String issuerUri;
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id:''}")
    private String clientId;
    @Value("${cloud.url:'http://localhost:3500'}")
    private String cloudUrl;
    PathPatternRequestMatcher.Builder mvc = PathPatternRequestMatcher.withDefaults();

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
            CloudAuthTokenHeaderFilter cloudAuthTokenHeaderFilter,
            CloudLogoutHandler cloudLogoutHandler) throws Exception {

        http.headers(header -> header.frameOptions(
                frameOptions -> frameOptions.disable().cacheControl(HeadersConfigurer.CacheControlConfig::disable)));
        http.cors(Customizer.withDefaults());
        http.addFilterBefore(cloudAuthTokenHeaderFilter, BasicAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CloudSpaCsrfTokenRequestHandler())
                .ignoringRequestMatchers(
                        mvc.matcher("/api/**"),
                        mvc.matcher(ERROR_PATH),
                        mvc.matcher("/logout"),
                        mvc.matcher("/h2/**")))
                .addFilterAfter(new CloudCsrfCookieFilter(), BasicAuthenticationFilter.class);
        http.sessionManagement(sess -> sess
                .maximumSessions(1));
        String keycloakUrlFormat = String.format(
                "%s/protocol/openid-connect/logout?client_id=%s&post_logout_redirect_uri=%s",
                issuerUri, clientId, cloudUrl);
        http.oauth2Login(withDefaults());
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers(
                    mvc.matcher(ERROR_PATH),
                    mvc.matcher("/api/discovery"),
                    mvc.matcher("/assets/**"),
                    mvc.matcher("/favicon.ico"),
                    mvc.matcher("/*.png"),
                    mvc.matcher("/manifest.json"),
                    mvc.matcher("/swagger-resources/**"),
                    mvc.matcher("/browserconfig.xml"),
                    mvc.matcher("/api/**")).permitAll();
            authorizeRequests.anyRequest().authenticated();
        });
        http.logout(logout -> logout.addLogoutHandler(cloudLogoutHandler)
                .logoutSuccessUrl(keycloakUrlFormat));
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(allowUrlEncodedSlaturHttpFirewall()).ignoring()
                .requestMatchers(mvc.matcher("/h2/**"));
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean(name = "passwordEncoder")
    PasswordEncoder passwordencoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    HttpFirewall allowUrlEncodedSlaturHttpFirewall() {
        // Allow double slash in URL
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER");
    }

    @Bean
    public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

}
