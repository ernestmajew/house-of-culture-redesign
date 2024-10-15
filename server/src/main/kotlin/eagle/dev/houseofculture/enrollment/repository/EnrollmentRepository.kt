package eagle.dev.houseofculture.enrollment.repository

import eagle.dev.houseofculture.enrollment.model.Enrollment
import eagle.dev.houseofculture.event.model.Event
import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.user.model.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant
import java.time.LocalDate

interface EnrollmentRepository : JpaRepository<Enrollment, Long> {

    @Query("select e from Enrollment e where e.client = ?1 and e.event.ends >= ?2 and e.event.starts <= ?3")
    fun findByClientAndBetweenDates(
        client: User,
        rangeStart: Instant,
        rangeEnd: Instant
    ): List<Enrollment>

    @Query(""" 
        SELECT DISTINCT (e.event.event)
        FROM Enrollment e
        LEFT JOIN SingleEvent se ON e.event = se
        WHERE e.client.id = :userId
            AND (
                (e.payment IS NULL AND e.event.event.cost <> NULL)
                OR se.ends >= :currentDate
            )
    """)
    fun findEventsByUserIdWhichAreAfterDateOrIsNotPaid(
        @Param("userId") userId: Long,
        @Param("currentDate") currentDate: Instant
    ): List<Event>

    @Query("""
        SELECT DISTINCT (e.event.event)
        FROM Enrollment e
        WHERE e.client.id = :userId
    """)
    fun findAllEventsByUserId(@Param("userId") userId: Long): List<Event>

    @Query("""
        SELECT COUNT(se.starts)
        FROM Enrollment e
        JOIN e.event se
        WHERE e.client.id = :userId
            AND e.event.event = :event
            AND se.starts >= :currentDate
    """)
    fun getNumberOfEnrollmentsForEventForUser(
        @Param("userId") userId: Long,
        @Param("event") event: Event,
        @Param("currentDate") currentDate: Instant): Int

    @Query("""
        SELECT MIN(DATE(se.starts))
        FROM Enrollment e
        JOIN e.event se
        WHERE e.client.id = :userId
            AND e.event.event = :event
    """)
    fun findMinStartDateFromEvent(@Param("userId") userId: Long, @Param("event") event: Event): LocalDate

    @Query("""
        SELECT MAX(DATE(se.ends))
        FROM Enrollment e
        JOIN e.event se
        WHERE e.client.id = :userId
            AND e.event.event = :event
    """)
    fun findMaxEndDateFromEvent(@Param("userId") userId: Long, @Param("event") event: Event): LocalDate

    @Query("""
        SELECT SUM(event.cost) FROM Enrollment enrollment
        JOIN enrollment.event.event event
        WHERE enrollment IN ?1
        AND event.cost IS NOT NULL
    """)
    fun getTotalCostOfEnrollments(enrollments: List<Enrollment>): Double

    @Query("""
        SELECT e
        FROM Enrollment e
        JOIN e.event se
        WHERE e.client.id = :userId
            AND e.event.event = :event
            AND se.starts >= :currentDate
    """)
    fun findLastFutureClientEnrollments(
        @Param("userId") userId: Long,
        @Param("event") event: Event,
        @Param("currentDate") currentDate: Instant,
        pageable: Pageable
    ): List<Enrollment>

    @Query("""
        SELECT COUNT(e) FROM Enrollment e LEFT JOIN e.payment p
        WHERE (p IS NULL OR p.status = 'CANCELED')
        AND e.client = ?1
        AND e.event.event = ?2
        AND e.event.event.cost IS NOT NULL
    """)
    fun countUnpaidUserEnrollmentsInEvent(user: User, event: Event): Int

    @Query("""
        SELECT e FROM Enrollment e LEFT JOIN e.payment p
        WHERE (p IS NULL OR p.status = 'CANCELED')
        AND e.client = ?1
        AND e.event.event = ?2
        AND e.event.event.cost IS NOT NULL
    """)
    fun getUnpaidEnrollmentsInEventOfUser(user: User, event: Event, pageable: Pageable): List<Enrollment>

    @Query("""
        SELECT e 
        FROM Enrollment e 
        LEFT JOIN e.payment p
        WHERE (p <> NULL)
            AND e.client = ?1
            AND e.event.event = ?2
            AND e.event.event.cost IS NOT NULL
        ORDER BY p.time DESC
    """)
    fun getPaidEnrolmentsForEventsOfUser(user: User, event: Event): List<Enrollment>

    fun existsByClientAndEventEvent(user: User, event: Event): Boolean

    @Query(
        """
        SELECT e FROM Enrollment e
        LEFT JOIN e.payment p
        WHERE (p IS NULL OR p.status = 'CANCELED')
        AND e.event.event.cost IS NOT NULL
        AND e.client IN :users
        AND e.event.ends >= :start
        AND e.event.starts <= :end
    """
    )
    fun getUnpaidEnrollmentsInRangeForUsers(
        @Param("users") users: List<User>,
        @Param("start") start: Instant,
        @Param("end") end: Instant
    ): List<Enrollment>


    @Query("""
            SELECT DISTINCT e.client
            FROM Enrollment e
            WHERE e.event.event = :event
        """)
    fun getAllEnrolledUserForActivity(@Param("event") event: Event): List<User>

    @Query("""
            SELECT DISTINCT e.client
            FROM Enrollment e
            WHERE e.event.event = :event
                AND e.event.ends >= :currentDate
        """)
    fun getAllEnrolledUserForActivityWhichAreAfterCurrentDate(
        @Param("event") event: Event,
        @Param("currentDate") currentDate: Instant
    ): List<User>


    @Query("""
            SELECT e
            FROM Enrollment e
            LEFT JOIN SingleEvent se ON e.event = se
            LEFT JOIN e.payment p
            WHERE e.client.id = :userId
                AND se.event = :event
                AND se.starts >= :currentDate
                AND (p IS NULL OR p.status = 'CANCELED')
        """)
    fun getFutureEnrollmentsForEventForUser(
        @Param("userId") userId: Long,
        @Param("event") event: Event,
        @Param("currentDate") currentDate: Instant): List<Enrollment>
}