package eagle.dev.houseofculture.auth.repository

import eagle.dev.houseofculture.auth.model.PasswordChange
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PasswordChangeRepository: JpaRepository<PasswordChange, UUID> {
}