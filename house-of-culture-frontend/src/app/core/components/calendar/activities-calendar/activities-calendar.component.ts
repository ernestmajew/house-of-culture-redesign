import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CalendarComponent} from "../calendar.component";
import {CalendarEvent} from "angular-calendar";
import {SingleEventOccurenceTs} from "../../../../../../out/api";
import {colors} from "../calendar.colors";

@Component({
  selector: 'app-activities-calendar',
  standalone: true,
  imports: [CommonModule, CalendarComponent],
  templateUrl: './activities-calendar.component.html',
  styleUrls: ['./activities-calendar.component.scss']
})
export class ActivitiesCalendarComponent implements OnChanges{
  @Input() events: SingleEventOccurenceTs[] = [];
  @Input() eventClick: (event: {event: CalendarEvent}) => void = () => {};
  eventsToCalendar: CalendarEvent[] = [];

  ngOnChanges(changes:SimpleChanges) {
    if (changes['events']) {
      this.events = changes['events'].currentValue;
      this.eventsToCalendar = this.mapToCalendarEvent(this.events)
    }
  }


  private mapToCalendarEvent(events: SingleEventOccurenceTs[]): CalendarEvent[] {
    return events.map(event => {
      return {
        start: new Date(event.startTime),
        end: new Date(event.endTime),
        title: this.getTitleOfEvent(event),
        color: event.isCancelled ? colors.red : colors.primary,
        meta: event
      } as CalendarEvent
    })
  }

  private getTitleOfEvent(event: SingleEventOccurenceTs): string {
    return event.isCancelled ? `Anulowany | ${event.title}` : `${event.title}`;
  }
}
