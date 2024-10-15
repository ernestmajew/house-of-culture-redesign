package eagle.dev.houseofculture.event.repository

import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.EventStatus
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.user.model.Client
import eagle.dev.houseofculture.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface SingleEventRepository : JpaRepository<SingleEvent, Long> {
    @Query(
        """
        SELECT DISTINCT singleEvent FROM SingleEvent singleEvent
        LEFT JOIN singleEvent.enrollments enrollment
        WHERE singleEvent.id NOT IN (
            SELECT s.id FROM SingleEvent s JOIN s.enrollments e
            WHERE e.client = ?1 AND s.event = ?3
        )
        AND singleEvent.starts > ?2
        AND (SIZE(singleEvent.enrollments) < singleEvent.event.maxPlaces OR singleEvent.event.maxPlaces IS NULL)
        AND singleEvent.event = ?3
        AND singleEvent.isCancelled = false
        ORDER BY singleEvent.starts
    """
    )
    fun getAvailableSingleEventsToEnroll(user: User, after: Instant, event: Event): List<SingleEvent>

    @Query("""
        SELECT singleEvent
        FROM SingleEvent singleEvent
        JOIN singleEvent.event event
        WHERE event.instructor = ?1 
            AND singleEvent.ends >= ?2 
            AND singleEvent.starts <= ?3
            AND event.status = ?4
            AND singleEvent.isCancelled = false
        """)
    fun findByInstructorAndBetween(
        instructor: Client,
        rangeStart: Instant,
        rangeEnd: Instant,
        eventStatus: EventStatus
    ): List<SingleEvent>

    @Query("""
        SELECT e.event 
        FROM Enrollment e 
        WHERE e.client = ?1 
            AND e.event.ends >= ?2 
            AND e.event.starts <= ?3
            AND e.event.event.status = ?4
            AND e.event.isCancelled = false
        """)
    fun findByClientAndBetweenDates(
        client: User,
        rangeStart: Instant,
        rangeEnd: Instant,
        eventStatus: EventStatus
    ): List<SingleEvent>
}