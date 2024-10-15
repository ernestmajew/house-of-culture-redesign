package eagle.dev.houseofculture.event.util

import eagle.dev.houseofculture.category.util.CategoryMapper
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.openapi.model.ActivityResponseTs
import eagle.dev.houseofculture.openapi.model.ActivitySummaryResponseTs
import eagle.dev.houseofculture.openapi.model.EventInfoTs
import eagle.dev.houseofculture.openapi.model.SingleEventOccurenceTs
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.util.mapper.CommonMapper
import eagle.dev.houseofculture.util.toLocalDateWithoutZone
import eagle.dev.houseofculture.util.toOffsetDateTimeWithoutZone
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import java.time.LocalDate
import java.time.OffsetDateTime

@JvmDefaultWithCompatibility // needed to use java "public default" scope on methods
@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = [CategoryMapper::class, CommonMapper::class]
)
interface EventMapper {

    @Mapping(source = "activity.singleEvents", target = "occurences", qualifiedByName = ["getSortedOccurrences"])
    @Mapping(source = "activity.instructor.id", target = "instructorId")
    @Mapping(source = "activity.instructor", target = "instructorName", qualifiedByName = ["getInstructorFullName"])
    @Mapping(source = "activity.singleEvents", target = "closestAvailableSingleEventDate", qualifiedByName = ["getClosestFreeSingleEventDate"])
    fun activityToTs(activity: Event, images: List<String>): ActivityResponseTs

    @Mapping(source = "starts", target = "startTime", qualifiedByName = ["instantToOffsetDateTime"])
    @Mapping(source = "ends", target = "endTime", qualifiedByName = ["instantToOffsetDateTime"])
    @Mapping(source = "cancelled", target = "isCancelled")
    @Mapping(source = "event.instructor.id", target = "instructorId")
    @Mapping(source = "event.title", target = "title")
    @Mapping(source = "event.instructor", target = "instructorName", qualifiedByName = ["getInstructorFullName"])
    @Mapping(source = "event.id", target = "mainEventId")
    fun singleEventToOccurrenceResponse(singleEvents: SingleEvent): SingleEventOccurenceTs

    @Mapping(source = "activity.instructor.id", target = "instructorId")
    @Mapping(source = "activity.instructor", target = "instructorName", qualifiedByName = ["getInstructorFullName"])
    @Mapping(source = "activity.singleEvents", target = "startDate", qualifiedByName = ["getStartDate"])
    @Mapping(source = "activity.singleEvents", target = "endDate", qualifiedByName = ["getEndDate"])
    fun activityToSummaryTs(activity: Event, image: String): ActivitySummaryResponseTs

    fun eventToInfoTs(event: Event): EventInfoTs

    @Named("getInstructorFullName")
    fun getInstructorFullName(instructor: Client?): String =
        instructor!!.let { "${it.firstName} ${it.lastName}" }

    @Named("getClosestFreeSingleEventDate")
    fun getClosestFreeSingleEventDate(singleEvents: List<SingleEvent>): OffsetDateTime? =
        singleEvents.filter { it.availablePlaces != 0 }
            .minByOrNull { it.starts }
            ?.starts?.toOffsetDateTimeWithoutZone()

    @Named("getStartDate")
    fun getStartDate(singleEvents: List<SingleEvent>): LocalDate =
        singleEvents.minBy { it.starts }.starts.toLocalDateWithoutZone()

    @Named("getEndDate")
    fun getEndDate(singleEvents: List<SingleEvent>): LocalDate =
        singleEvents.maxBy { it.starts }.starts.toLocalDateWithoutZone()

    @Named("getSortedOccurrences")
    fun getSortedOccurrences(singleEvents: List<SingleEvent>): List<SingleEventOccurenceTs> =
        singleEvents.sortedBy { it.ends }.map { singleEventToOccurrenceResponse(it) }
}