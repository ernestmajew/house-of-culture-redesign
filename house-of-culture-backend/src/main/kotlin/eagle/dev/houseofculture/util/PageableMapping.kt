package eagle.dev.houseofculture.util

import eagle.dev.houseofculture.openapi.model.MinimalPageableTs
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

fun MinimalPageableTs.toPageRequest(sort: Sort) =
    PageRequest.of(this.page, this.pageSize, sort)