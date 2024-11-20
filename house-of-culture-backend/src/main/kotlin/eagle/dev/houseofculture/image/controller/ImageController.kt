package eagle.dev.houseofculture.image.controller

import eagle.dev.houseofculture.image.service.ImageService
import eagle.dev.houseofculture.openapi.api.ImagesApi
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
class ImageController(
    val imageService: ImageService,
    val httpServletRequest: HttpServletRequest
): ImagesApi {

    override fun getImage(): ResponseEntity<Resource> {
        val imagePath = httpServletRequest.requestURI.substringAfter("image/")
        return ResponseEntity.ok(imageService.getImage(imagePath))
    }
}