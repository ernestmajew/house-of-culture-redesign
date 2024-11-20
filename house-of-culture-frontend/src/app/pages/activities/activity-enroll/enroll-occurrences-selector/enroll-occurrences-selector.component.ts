import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {CalendarEvent, CalendarView} from "angular-calendar";
import {SingleEventOccurenceTs} from "../../../../../../out/api/model/single-event-occurence";
import {colors} from "../../../../core/components/calendar/calendar.colors";

@Component({
  selector: 'app-enroll-occurrences-selector',
  templateUrl: './enroll-occurrences-selector.component.html',
  styleUrls: ['./enroll-occurrences-selector.component.scss']
})
export class EnrollOccurrencesSelectorComponent implements OnChanges {
  @Input() selectedNumberOfEvents!: number
  @Input() occurrences!: SingleEventOccurenceTs[] // sorted by startTime already in response

  // if closestAvailableSingleEventDate is undefined then there are no available places
  firstOccurrenceTime: Date = new Date()
  calendarEvents: CalendarEvent[] = []

  ngOnChanges(changes: SimpleChanges): void {
    if(changes['occurrences'] || changes['selectedNumberOfEvents']) {
      this.buildCalendarEvents()

      if(this.occurrences.length)
        this.firstOccurrenceTime = new Date(this.occurrences[0].startTime) // set view on calendar
    }
  }

  buildCalendarEvents() {
    this.calendarEvents = this.occurrences
      .map(event => ({
          start: new Date(event.startTime),
          end: new Date(event.endTime),
          title: "[NIE WYBRANO]",
          id: event.id,
          color: colors.blue
        } as CalendarEvent)
      ).map(this.updateEventWithSelection)
  }

  private updateEventWithSelection = (event: CalendarEvent, index: number): CalendarEvent => {
    const isSelected = index < this.selectedNumberOfEvents

    return {...event,
      color: isSelected ? colors.primary : undefined,
      title: isSelected ? "[WYBRAN0]" : "[NIE WYBRANO]"
    }
  }

  protected readonly CalendarView = CalendarView;
}
