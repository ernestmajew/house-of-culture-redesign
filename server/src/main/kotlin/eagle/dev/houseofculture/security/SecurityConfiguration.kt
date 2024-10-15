package eagle.dev.houseofculture.security

import eagle.dev.houseofculture.security.csrf.SpaCsrfTokenRequestHandler
import eagle.dev.houseofculture.security.jwt.JwtAuthFilter
import eagle.dev.houseofculture.user.model.enumeration.UserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Profile("!test")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
class SecurityConfiguration(
    private var authFilter: JwtAuthFilter,
    private var userDetailsService: UserDetailsService,
    @Value("\${app.url.frontend}") private val defaultFrontendUrl: String
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf {
            //TODO: CSRF protection
//            it.csrfTokenRepository(csrfTokenRepository())
//            it.csrfTokenRequestHandler(csrfTokenRequestHandler())
            it.disable()
        }

            .headers { it ->
                it.frameOptions {
                    it.sameOrigin()
                }
            }
            .cors{
                it.configurationSource(corsConfigurationSource())
            }
            .authorizeHttpRequests() {
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Public endpoints
                it.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").permitAll()
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/api/public/**").permitAll()
                it.requestMatchers("/api/payu/**", "/api/payu").permitAll()
                it.requestMatchers(HttpMethod.POST, "/api/payu/**", "/api/payu").permitAll()

                // Admin endpoints
                it.requestMatchers("/api/admin/**").hasAuthority(UserRole.ADMIN.name)

                // Employee endpoints
                it.requestMatchers(HttpMethod.POST, "/api/activity/**", "/api/activity")
                    .hasAuthority(UserRole.EMPLOYEE.name)
                it.requestMatchers(HttpMethod.PUT, "/api/activity/**", "/api/activity")
                    .hasAuthority(UserRole.EMPLOYEE.name)
                it.requestMatchers(HttpMethod.DELETE, "/api/enrollments/participants/**")
                    .hasAuthority(UserRole.EMPLOYEE.name)

                // Instructor endpoints
                it.requestMatchers(HttpMethod.POST, "/api/post", "/api/category")
                    .hasAuthority(UserRole.INSTRUCTOR.name)
                it.requestMatchers(HttpMethod.DELETE, "/api/category/**", "/api/post/**")
                    .hasAuthority(UserRole.INSTRUCTOR.name)
                it.requestMatchers(HttpMethod.PUT, "/api/post/**")
                    .hasAuthority(UserRole.INSTRUCTOR.name)
                it.requestMatchers("/api/instructor/**")
                    .hasAuthority(UserRole.INSTRUCTOR.name)

                // Client endpoints
                it.requestMatchers(HttpMethod.POST, "/api/user", "/api/enrollment/**")
                    .hasAuthority(UserRole.CLIENT.name)
                it.requestMatchers(HttpMethod.GET, "/api/enrollment")
                    .hasAuthority(UserRole.CLIENT.name)
                it.requestMatchers(HttpMethod.DELETE, "/api/enrollment/**")
                    .hasAuthority(UserRole.CLIENT.name)

                // Rest of the endpoints
                it.requestMatchers("/api/**").authenticated()
                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider())
            .addFilterAfter(authFilter, UsernamePasswordAuthenticationFilter::class.java)
            .logout {
                it.logoutUrl("api/auth/logout")
                it.deleteCookies("JSESSIONID", "XSRF-TOKEN")
                it.logoutSuccessHandler { _: HttpServletRequest, _: HttpServletResponse, _: Authentication -> SecurityContextHolder.clearContext() }
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf(this.defaultFrontendUrl)
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun csrfTokenRepository(): CsrfTokenRepository {
        return CookieCsrfTokenRepository.withHttpOnlyFalse()
    }

    @Bean
    fun csrfTokenRequestHandler(): CsrfTokenRequestHandler {
        return SpaCsrfTokenRequestHandler()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider? {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        return authenticationProvider
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }
}