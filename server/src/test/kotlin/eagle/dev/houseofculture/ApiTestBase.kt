package eagle.dev.houseofculture

import eagle.dev.houseofculture.category.repository.CategoryRepository
import eagle.dev.houseofculture.contact.repository.ContactInfoRepository
import eagle.dev.houseofculture.enrollment.repository.EnrollmentRepository
import eagle.dev.houseofculture.enrollment.repository.OldEnrollmentRepository
import eagle.dev.houseofculture.enrollment.repository.PaymentRepository
import eagle.dev.houseofculture.event.repository.EventRepository
import eagle.dev.houseofculture.event.repository.SingleEventRepository
import eagle.dev.houseofculture.post.repository.PostRepository
import eagle.dev.houseofculture.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class ApiTestBase {
    @Autowired lateinit var contactRepository: ContactInfoRepository
    @Autowired lateinit var enrollmentRepository: EnrollmentRepository
    @Autowired lateinit var oldEnrollmentRepository: OldEnrollmentRepository
    @Autowired lateinit var paymentRepository: PaymentRepository
    @Autowired lateinit var eventRepository: EventRepository
    @Autowired lateinit var categoryRepository: CategoryRepository
    @Autowired lateinit var singleEventRepository: SingleEventRepository
    @Autowired lateinit var postRepository: PostRepository
    @Autowired lateinit var userRepository: UserRepository


}