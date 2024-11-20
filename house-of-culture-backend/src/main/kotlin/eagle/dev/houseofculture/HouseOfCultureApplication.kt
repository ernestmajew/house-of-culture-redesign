package eagle.dev.houseofculture

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class HouseOfCultureApplication

fun main(args: Array<String>) {
	runApplication<HouseOfCultureApplication>(*args)
}
