package eagle.dev.houseofculture.event.controller

import eagle.dev.houseofculture.event.service.ActivityService
import eagle.dev.houseofculture.openapi.api.ActivityApi
import eagle.dev.houseofculture.openapi.model.*
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController
class ActivityController(
    private val activityService: ActivityService,
) : ActivityApi {
    override fun createActivity(createActivityRequestTs: CreateActivityRequestTs): ResponseEntity<Long> =
        activityService
            .createActivity(createActivityRequestTs)
            .let { ResponseEntity.ok(it) }

    override fun getActivityById(id: Long): ResponseEntity<ActivityResponseTs> =
        activityService
            .getActivity(id)
            .let { ResponseEntity.ok(it) }

    override fun getActivitiesCategories(
        number: Int?
    ): ResponseEntity<List<CategoryResponseTs>> =
        activityService
            .getActivitiesCategories(number)
            .let { ResponseEntity.ok(it) }

    override fun getPopularActivities(): ResponseEntity<PopularActivitiesResponseTs> =
        activityService
            .getPopularActivities()
            .let { ResponseEntity.ok(it) }

    override fun getActivities(
        page: Int,
        pageSize: Int,
        text: String,
        category: Long?
    ): ResponseEntity<FilteredActivitiesResponseTs> =
        activityService
            .getActivities(page, pageSize, text, category)
            .let { ResponseEntity.ok(it) }

    override fun editActivity(id: Long, editActivityRequestTs: EditActivityRequestTs): ResponseEntity<Long> =
        activityService
            .editActivity(id, editActivityRequestTs)
            .let { ResponseEntity.ok(it) }

    override fun deleteActivity(id: Long): ResponseEntity<Unit> {
        activityService.deleteActivity(id)
        return noContentResponse()
    }

    override fun getInstructorActivity(): ResponseEntity<List<ActivitySummaryResponseTs>> {
        return ResponseEntity.ok(activityService.getInstructorActivity())
    }

    private fun noContentResponse(): ResponseEntity<Unit> {
        return ResponseEntity.noContent().build()
    }
}