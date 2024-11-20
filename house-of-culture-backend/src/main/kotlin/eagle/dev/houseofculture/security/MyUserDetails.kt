package eagle.dev.houseofculture.security

import eagle.dev.houseofculture.user.model.User
import eagle.dev.houseofculture.user.model.enumeration.UserStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class MyUserDetails(private val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return user.role.getAllRoles().map { role -> SimpleGrantedAuthority(role.name) }
    }

    override fun getPassword(): String {
        return user.password;
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isAccountNonExpired(): Boolean {
        return user.status === UserStatus.ACTIVE
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}