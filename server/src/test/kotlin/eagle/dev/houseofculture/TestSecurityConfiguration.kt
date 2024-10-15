package eagle.dev.houseofculture.util

import eagle.dev.houseofculture.security.jwt.JwtService
import lombok.RequiredArgsConstructor
import org.mockito.Mockito
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Profile("test")
@TestConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
class TestSecurityConfiguration() {

    @Bean
    fun jwtService(): JwtService {
        return Mockito.mock(JwtService::class.java)
    }

    @Bean
    fun testFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf {
            it.disable()
        }
            .authorizeHttpRequests() {
                it.requestMatchers("/api/**").permitAll()
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/api/public/**").permitAll()
                it.requestMatchers("/api/payu/**").permitAll()
                it.requestMatchers("/api/payu").permitAll()
                it.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html").permitAll()
                it.anyRequest().authenticated()
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider? {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService())
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        return authenticationProvider
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        return Mockito.mock(UserDetailsService::class.java)
    }
}
