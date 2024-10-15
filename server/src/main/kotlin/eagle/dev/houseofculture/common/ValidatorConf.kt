package eagle.dev.houseofculture.common

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.context.annotation.Bean

class ValidatorConf {

    @Bean
    fun validator(): Validator? {
        val validatorFactory = Validation.buildDefaultValidatorFactory();
        val validator = validatorFactory.getValidator();
        return validator;
    }
}