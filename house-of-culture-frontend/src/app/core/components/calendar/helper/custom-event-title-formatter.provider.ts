import { LOCALE_ID, Inject, Injectable } from '@angular/core';
import { CalendarEventTitleFormatter, CalendarEvent } from 'angular-calendar';
import { formatDate } from '@angular/common';

@Injectable()
export class CustomEventTitleFormatter extends CalendarEventTitleFormatter {
  constructor(@Inject(LOCALE_ID) private locale: string) {
    super();
  }

  // you can override any of the methods defined in the parent class

  override month(event: CalendarEvent): string {
    return this.getTooltip(event);
  }

  override week(event: CalendarEvent): string {
    return this.getTooltip(event);
  }

  override day(event: CalendarEvent): string {
    return this.getTooltip(event);
  }

  private getTooltip(event: CalendarEvent): string {
    return `<b>[${this.getFormattedDate(event.start)}-${this.getFormattedDate(event.end)}]
            </br>
            <span>${event.title}</span>`;
  }

  private getFormattedDate(date?: Date) {
    if(!date) return '';
    return formatDate(date, 'HH:mm', this.locale)
  }
}
