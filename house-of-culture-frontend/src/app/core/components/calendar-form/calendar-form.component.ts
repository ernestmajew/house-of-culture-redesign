import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CalendarEvent} from "angular-calendar";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {BaseComponent} from "../../abstract-base/base.component";
import {NotificationMessageType} from "../../models/notification-message";
import {colors} from "../calendar/calendar.colors";
import {Frequency} from "../../../pages/administration/create-new-activities/new-activities-form.component";
import {SingleEventService} from "../../../pages/activities/services/single-event.service";
import {take} from "rxjs/operators";
import {EditedEvent} from "../edit-single-event-popup/edit-single-event-popup.component";
import {CreateSingleEventOccurenceTsFrequencyEnum} from 'out/api';

export interface Event {
  id: number;
  startDate: Date;
  repeatNumber: number;
  frequency: Frequency;
  startTime: string;
  endTime: string;
  isCanceled?: boolean;
}

@Component({
  selector: 'app-calendar-form',
  templateUrl: './calendar-form.component.html',
  styleUrls: ['./calendar-form.component.scss']
})
export class CalendarFormComponent extends BaseComponent implements OnInit {
  @Input() events?: Event[];
  @Input() eventTitle?: string;
  @Input() isThisEdit?: boolean;

  form: FormGroup;

  eventsToCards: Event[] = [];
  eventsToCalendar: CalendarEvent[] = [];

  @Input() frequencies!: Frequency[]
  @Output() emitEventsChange: EventEmitter<Event[]> = new EventEmitter<Event[]>();

  constructor(private service: SingleEventService) {
    super();
    this.form = new FormGroup({
      startDate: new FormControl('', Validators.required),
      repeatCount: new FormControl(undefined),
      frequency: new FormControl(undefined),
      startTime: new FormControl('', Validators.required),
      endTime: new FormControl('', Validators.required),
    });
  }

  ngOnInit(): void {
    if (this.events) {
      this.eventsToCards = [...this.events];
      this.events.map(event => {
        this.addToCalendar(this.createCalendarEvents(event));
      })
    }
  }

  eventEdited(event: { updatedEventInfo: EditedEvent, event: Event }) {
    const {updatedEventInfo, event: originalEvent} = event;

    // Create a copy of the original event with updated properties
    const newEvent: Event = {
      ...originalEvent,
      startDate: updatedEventInfo.startDate,
      startTime: updatedEventInfo.startTime,
      endTime: updatedEventInfo.endTime,
      isCanceled: updatedEventInfo.isCanceled,
    };

    this.removeEvent(this.eventsToCards.findIndex(e => e.id === originalEvent.id));
    const requestedEvents: CalendarEvent[] = this.createCalendarEvents(newEvent);

    if (this.validateGivenData(requestedEvents)) {
      if (this.isThisEdit) {
        this.service.updateSingleEvent(updatedEventInfo).pipe(take(1)).subscribe({
          next: () => {
            this.addToCard(newEvent);
            this.addToCalendar(requestedEvents);
            this.messageService.sendMessage(
              "Pojedyńcze zajęcia zostały pomyślnie wyedytowane.",
              NotificationMessageType.SUCCESS
            );
          },
          error: (error) => {
            this.messageService.sendMessage("Podany czas zajęć nachodzi na harmonogram innych zajęć.", NotificationMessageType.ERROR);
          }
        });
      } else {
        this.addToCard(newEvent);
        this.addToCalendar(requestedEvents);
        this.messageService.sendMessage(
          "Pojedyńcze zajęcia zostały pomyślnie wyedytowane.",
          NotificationMessageType.SUCCESS
        );
      }
    } else {
      this.addToCard(originalEvent);
      this.addToCalendar(this.createCalendarEvents(originalEvent));
    }
  }

  addEvent() {
    const startDateFormValue = this.form.get("startDate")?.value;
    const frequencyFormValue = this.form.get("frequency")?.value;
    const repeatFormValue = this.form.get("repeatCount")?.value;
    const startTimeFormValue = this.form.get("startTime")?.value;
    const endTimeFormValue = this.form.get("endTime")?.value;

    const frequency = frequencyFormValue || {
      number: 1,
      name: undefined,
      enumValue: CreateSingleEventOccurenceTsFrequencyEnum.SINGLE
    };

    const repeat = this.getRepeatCount(repeatFormValue, frequency);

    const uniqueId = Date.now();

    let event = {
      id: uniqueId,
      startDate: new Date(startDateFormValue),
      frequency: frequency,
      repeatNumber: repeat,
      startTime: startTimeFormValue,
      endTime: endTimeFormValue
    } as Event

    let requestedEvents: CalendarEvent[] = this.createCalendarEvents(event);

    if (this.validateGivenData(requestedEvents)) {

      if (frequencyFormValue === null && repeatFormValue === null) {
        this.messageService.sendMessage(
          "Nie zdefiniowano częstotliwości więc zajęcia zostały ustawione jako jednorazowe",
          NotificationMessageType.INFO
        );
      } else if (frequency.number !== 1 && repeatFormValue === null) {
        this.messageService.sendMessage(
          "Nie zdefiniowano ilości powtórzeń więc zajęcia zostały ustawione na domyślny okres 3 miesięcy.",
          NotificationMessageType.INFO
        );
      }

      this.addToCard(event);
      this.addToCalendar(requestedEvents);
      this.form.reset();
    }
  }

  addToCalendar(events: CalendarEvent[]) {
    this.eventsToCalendar = [...this.eventsToCalendar, ...events];
  }

  addToCard(event: Event) {
    this.eventsToCards.push(event);
    this.sortListOfEvents(this.eventsToCards);
    this.emitEventsChange.emit(this.eventsToCards);
  }

  removeEvent(index: number) {
    if (this.eventsToCards && this.eventsToCards.length > index) {
      let removedEvent = this.eventsToCards[index];
      this.eventsToCards.splice(index, 1);

      // Remove all occurrences of the event with the specified id from eventsToCalendar
      this.eventsToCalendar = this.eventsToCalendar.filter((calendarEvent) => {
        return calendarEvent.id !== removedEvent.id;
      });
    }
    this.emitEventsChange.emit(this.eventsToCards);
  }

  private validateGivenData(events: CalendarEvent[]): boolean {
    // Check if start time is before end time
    if (events[0].start >= (events[0].end!)) {
      this.messageService.sendMessage(
        "Czas rozpoczęcia zajęć nie może być taki sam jak czas zakończenia zajęć lub późniejszy niż czas zakończenia zajęć",
        NotificationMessageType.ERROR
      )
      return false;
    }

    // Check for overlapping events
    const eventsToCalendar = [...this.eventsToCalendar]; //copy of current events
    const allEvents = [...events, ...eventsToCalendar].sort((a, b) => a.start.getTime() - b.start.getTime());

    for (let i = 0; i < allEvents.length - 1; i++) {
      const currentEvent = allEvents[i];
      const nextEvent = allEvents[i + 1];

      if (currentEvent.end && currentEvent.end.getTime() > nextEvent.start.getTime()) {
        this.messageService.sendMessage(
          "Dodane zajęcia nachodzą na terminy innych zaplanowanych zajęć",
          NotificationMessageType.ERROR
        )
        return false;
      }
    }
    return true;
  }

  private createCalendarEvents(event: Event) {
    let calendarEvents: CalendarEvent[] = []

    const start = event.startDate;
    const [startHour, startMinute] = event.startTime.split(':').map(Number);
    const [endHour, endMinute] = event.endTime.split(':').map(Number);

    for (let i = 0; i < event.repeatNumber; i++) {
      const eventDate = new Date(start.getTime() + i * event.frequency.number * 24 * 60 * 60 * 1000);
      eventDate.setHours(startHour, startMinute);

      const eventEndDate = new Date(eventDate);
      eventEndDate.setHours(endHour, endMinute);

      const calendarEvent: CalendarEvent = {
        start: eventDate,
        end: eventEndDate,
        title: this.eventTitle || "Wydarzenie",
        color: event.isCanceled ? colors.red : colors.primary,
        id: event.id
      };

      calendarEvents.push(calendarEvent);
    }

    return calendarEvents
  }

  private getRepeatCount(repeatNumber: number | undefined, frequency: Frequency) {
    return frequency.number === 1 ? 1 : repeatNumber || (frequency && Math.floor(90 / frequency.number)) || 1;
  }

  private sortListOfEvents(events: Event[]) {
    return events.sort((a, b) => {
      // Compare by startDate
      const dateComparison = a.startDate.getTime() - b.startDate.getTime();

      // If startDate is the same, compare by startTime
      if (dateComparison === 0) {
        return a.startTime.localeCompare(b.startTime);
      }
      return dateComparison;
    });
  }
}
