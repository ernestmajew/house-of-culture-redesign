import {Pipe, PipeTransform} from '@angular/core';
import {SingleEventOccurenceTs} from "../../../../../../out/api/model/single-event-occurence";

@Pipe({
  name: 'activityOccurrence'
})
export class ActivityOccurrencePipe implements PipeTransform {
  transform(value: SingleEventOccurenceTs, index: number): string {
    let startTime = new Date(value.startTime)
    let endTime = new Date(value.endTime)

    return `${index+1}. ${this.formatTime(startTime)}-${this.formatTime(endTime)} ${startTime.toLocaleDateString()}`
  }

  formatTime(date: Date) {
    const options = {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false, // Use a 24-hour format
    } as Intl.DateTimeFormatOptions;
    return date.toLocaleTimeString(undefined, options);
  }
}
