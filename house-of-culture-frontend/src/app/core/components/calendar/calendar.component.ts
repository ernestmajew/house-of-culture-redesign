import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Output,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import {CommonModule, NgSwitch} from '@angular/common';
import {CalendarEvent, CalendarEventTitleFormatter, CalendarModule, CalendarView} from "angular-calendar";
import {FormsModule} from "@angular/forms";
import {CalendarHeaderComponent} from "./header/calendar-header.component";
import {differenceInMinutes, startOfDay, startOfHour} from 'date-fns';
import {CalendarDateHelperService} from "./helper/calendar-date-helper.service";
import {CustomEventTitleFormatter} from "./helper/custom-event-title-formatter.provider";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [
    CommonModule,
    CalendarModule,
    NgSwitch,
    FormsModule,
    CalendarHeaderComponent
  ],
  templateUrl: './calendar.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  styleUrls: ['./calendar.component.scss'],
  providers: [
    {
      provide: CalendarEventTitleFormatter,
      useClass: CustomEventTitleFormatter,
    },
  ],
})
export class CalendarComponent implements AfterViewInit {
  @Input() events: CalendarEvent[] = [];
  @Input() viewDate: Date = new Date();
  @Input() view: CalendarView = CalendarView.Week;
  @Input() hideViewButtons = false;
  @Input() hideArrows = false;
  @Input() dayStartHour: number = 0;
  @Input() dayEndHour: number = 23;
  @Output() viewDateChange = new EventEmitter<Date>();
  @Input() eventClick: (event: {event: CalendarEvent}) => void = () => {};

  @ViewChild('scrollContainer') scrollContainer?: ElementRef<HTMLElement>;

  constructor(
    private dateHelperService: CalendarDateHelperService,
    public dialog: MatDialog
  ) {}

  ngAfterViewInit() {
    this.scrollToCurrentView();
  }

  private scrollToCurrentView() {
    if (this.view === CalendarView.Week || CalendarView.Day) {
      // each hour is 60px high, so to get the pixels to scroll it's just the amount of minutes since midnight
      const minutesSinceStartOfDay = differenceInMinutes(
        startOfHour(new Date()),
        startOfDay(new Date())
      );
      const headerHeight = this.view === CalendarView.Week ? 60 : 0;
      if(this.scrollContainer) {
        this.scrollContainer.nativeElement.scrollTop =
          minutesSinceStartOfDay + headerHeight;
      }
    }
  }
  onViewDateChange(viewDate: Date) {
    this.viewDate = viewDate;
    this.dateHelperService.dateChanged(viewDate);
  }
}
