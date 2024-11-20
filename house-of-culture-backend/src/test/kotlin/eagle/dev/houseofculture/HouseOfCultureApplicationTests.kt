package eagle.dev.houseofculture

import eagle.dev.houseofculture.util.TestSecurityConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [TestSecurityConfiguration::class])
class HouseOfCultureApplicationTests {

	@Test
	fun contextLoads() {
	}

}
