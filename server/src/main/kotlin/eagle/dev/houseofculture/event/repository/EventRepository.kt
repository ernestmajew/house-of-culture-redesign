package eagle.dev.houseofculture.event.repository

import eagle.dev.houseofculture.category.model.Category
import eagle.dev.houseofculture.enrollment.model.Enrollment
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.EventStatus
import eagle.dev.houseofculture.event.model.EventType
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface EventRepository : JpaRepository<Event, Long> {
    // Spring ignores paging for pageRequest == null
    @Query("""
            SELECT category
            FROM Event event 
            JOIN event.categories category
            LEFT JOIN event.singleEvents singleEvent
            LEFT JOIN singleEvent.enrollments enrollment
            WHERE event.type = :type
                AND (enrollment.enrollmentTime > :from OR enrollment IS NULL)
                AND event.status = :eventStatus
            GROUP BY category
            ORDER BY COUNT(enrollment.id) DESC
         """
    )
    fun findMostPopularAssociatedCategories(
        @Param("type") type: EventType,
        pageRequest: PageRequest?,
        @Param("from") from: Instant = Instant.now().minusSeconds(60*60*24*30),
        @Param("eventStatus") eventStatus: EventStatus = EventStatus.ACTIVE
        ): List<Category>

    @Query("""
            SELECT event
            FROM Event event
            JOIN event.categories category
            LEFT JOIN event.singleEvents singleEvent
            LEFT JOIN singleEvent.enrollments enrollment
            WHERE (enrollment.enrollmentTime > :from OR enrollment IS NULL)
                AND category = :category
                AND event.status = :eventStatus
            GROUP BY event
            ORDER BY COUNT(enrollment.id) DESC
            LIMIT 4
        """
    )
    fun findTop4ByCategoriesContaining(
        @Param("category") category: Category,
        @Param("from") from: Instant = Instant.now().minusSeconds(60*60*24*30),
        @Param("eventStatus") eventStatus: EventStatus = EventStatus.ACTIVE
    ): List<Event>

    @Query(
        """
            SELECT event
            FROM Event event 
            LEFT JOIN event.singleEvents singleEvent
            LEFT JOIN singleEvent.enrollments enrollment
            WHERE (enrollment.enrollmentTime > :from OR enrollment IS NULL)
                AND event.status = :eventStatus
            GROUP BY event
            ORDER BY COUNT(enrollment.id) DESC
            LIMIT 4
        """
    )
    fun findTop4By(
        @Param("from") from: Instant = Instant.now().minusSeconds(60 * 60 * 24 * 30),
        @Param("eventStatus") eventStatus: EventStatus = EventStatus.ACTIVE
    ): List<Event>

    fun findAllByTitleContainingAndCategoriesContainingAndStatus(
        title: String,
        category: Category,
        status: EventStatus,
        pageRequest: PageRequest
    ): Page<Event>

    fun findAllByTitleContainingAndStatus(
        title: String,
        status: EventStatus,
        pageRequest: PageRequest
    ): Page<Event>

    @Query(
        """
        SELECT DISTINCT singleEvent 
        FROM Event event 
        JOIN event.singleEvents singleEvent 
        WHERE event.instructor = :instructor
            AND event.status = :eventStatus
            AND singleEvent.isCancelled = false
        """
    )
    fun findAllNoCanceledSingleEventsByInstructor(
        @Param("instructor") instructor: Client,
        @Param("eventStatus") eventStatus: EventStatus = EventStatus.ACTIVE
    ): List<SingleEvent>

    @Query(
        """
        SELECT DISTINCT singleEvent 
        FROM SingleEvent singleEvent 
        JOIN singleEvent.event e 
        WHERE e.instructor = :instructor 
            AND e <> :excludedEvent
            AND e.status = :eventStatus
            AND singleEvent.isCancelled = false
        """
    )
    fun findAllNoCanceledSingleEventsByInstructorNotForEvent(
        @Param("instructor") instructor: Client,
        @Param("excludedEvent") excludedEvent: Event,
        @Param("eventStatus") eventStatus: EventStatus = EventStatus.ACTIVE
    ): List<SingleEvent>

    @Query("""
            SELECT COALESCE(SUM(se.event.cost), 0) 
            FROM Enrollment e 
            JOIN e.event se
            LEFT JOIN e.payment p
            WHERE (p IS NULL OR p.status = 'CANCELED')
                AND e.client.id = :userId
                AND se.event = :event
                AND se.event.cost IS NOT NULL
                AND se.ends <= :currentDate
                AND se.isCancelled = false
        """)
    fun getDebtOfUserTakingPartInActivitySingleEvents(
        @Param("userId") userId: Long,
        @Param("event") event: Event,
        @Param("currentDate") currentDate: Instant
    ): Double


    @Query("""
            SELECT DISTINCT e
            FROM Event e
            JOIN e.singleEvents se
            WHERE se.ends > :currentDate
                AND e.instructor = :user
                AND e.status = :eventStatus
                AND se.isCancelled = false
        """)
    fun getAllCurrentlyOngoingActivitiesForInstructor(
        @Param("user") user: User,
        @Param("currentDate") currentDate: Instant,
        @Param("eventStatus") eventStatus: EventStatus = EventStatus.ACTIVE
    ) : List<Event>

    @Query("""
            SELECT DISTINCT enrollments
            FROM Event e
            JOIN e.singleEvents se
            JOIN se.enrollments enrollments
        """)
    fun getEnrollmentsForEvent(
        @Param("event") event: Event
    ) : List<Enrollment>
}