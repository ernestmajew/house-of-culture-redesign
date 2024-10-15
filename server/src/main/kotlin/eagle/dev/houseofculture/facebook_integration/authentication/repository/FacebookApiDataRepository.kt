package eagle.dev.houseofculture.facebook_integration.authentication.repository

import eagle.dev.houseofculture.facebook_integration.authentication.model.FacebookApiData
import org.springframework.data.jpa.repository.JpaRepository

interface FacebookApiDataRepository: JpaRepository<FacebookApiData, Long>