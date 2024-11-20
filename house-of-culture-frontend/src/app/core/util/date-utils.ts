import {formatDate} from "@angular/common";


export function formatDateToLocaleISOString(date: Date){
  return formatDate(date, 'yyyy-MM-dd', 'en-US');
}

export function formatDateAndTime(date: Date, time: string) {
  const dateObj = new Date(date);

  const formattedTime = `${time}:00`;
  const formattedDate = `${dateObj.getFullYear()}-${(dateObj.getMonth() + 1).toString().padStart(2, '0')}-${dateObj.getDate().toString().padStart(2, '0')}`;

  return `${formattedDate}T${formattedTime}Z`;
}

export function parseUTCDateWithoutTimezoneOffset(dateString: string): Date {
  const parts = dateString.match(/\d+/g);
  if (!parts) {
    throw new Error("Invalid date format");
  }

  const [year, month, day, hours, minutes, seconds] = parts.map(Number);
  return new Date(Date.UTC(year, month - 1, day, hours - 1, minutes, seconds));
}


//we must take first week of the previous month and last week of the next month
export function getMonthEvents(date: Date): {start: Date, end: Date} {
  const lastDayOfPreviousMonth = new Date(date.getFullYear(), date.getMonth(), 0);
  const firstDayOfNextMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0);

  const firstDay = new Date(lastDayOfPreviousMonth);
  firstDay.setDate(firstDay.getDate() - lastDayOfPreviousMonth.getDay());

  const lastDay = new Date(firstDayOfNextMonth);
  lastDay.setDate(lastDay.getDate() + (7 - firstDayOfNextMonth.getDay()));

  return { start: firstDay, end: lastDay };
}
