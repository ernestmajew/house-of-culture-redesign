import {CalendarNativeDateFormatter, DateFormatterParams} from "angular-calendar";
import {formatDate} from "@angular/common";
import {Injectable} from "@angular/core";

@Injectable()
export class NumericDateFormatter extends CalendarNativeDateFormatter {


  override dayViewHour({ date, locale }: DateFormatterParams): string {
    return formatDate(date, 'HH:mm', <string>locale);
  }
  override weekViewHour({ date, locale }: DateFormatterParams): string {
    return this.dayViewHour({ date, locale });
  }
}
