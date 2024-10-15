package eagle.dev.houseofculture.util

import eagle.dev.houseofculture.event.model.SingleEvent
import eagle.dev.houseofculture.openapi.model.ContactInfoTs
import net.fortuna.ical4j.data.CalendarOutputter
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ComponentList
import net.fortuna.ical4j.model.Property
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.property.Description
import net.fortuna.ical4j.model.property.Geo
import net.fortuna.ical4j.model.property.Location
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import java.io.ByteArrayOutputStream

class CalendarExportUtil {
    companion object {
        fun getICSCalendar(singleEvents: List<SingleEvent>, contactInfo: ContactInfoTs): Resource {
            val listOfProperties = listOf(
                Geo(contactInfo.latitude?.toBigDecimal(), contactInfo.longitude?.toBigDecimal()),
                Location("${contactInfo.addressFirstLine}, ${contactInfo.addressSecondLine}")
            )

            val vEvents = singleEvents.map {
                createVEvent(it, listOfProperties)
            }

            return writeCalendarToResource(Calendar(ComponentList(vEvents)))
        }

        private fun createVEvent(singleEvent: SingleEvent, properties: List<Property>): VEvent {
            val vEvent = VEvent(
                singleEvent.starts.toOffsetDateTime(),
                singleEvent.ends.toOffsetDateTime(),
                singleEvent.event.title
            )
            vEvent.addAll(properties)
            vEvent.add(
                Description("${singleEvent.event.instructor?.firstName} ${singleEvent.event.instructor?.lastName}")
            )
            return vEvent
        }

        private fun writeCalendarToResource(calendar: Calendar): Resource {
            val icsByteArray = ByteArrayOutputStream()
            val outputter = CalendarOutputter()

            outputter.output(calendar, icsByteArray)

            return ByteArrayResource(icsByteArray.toByteArray())
        }
    }
}
