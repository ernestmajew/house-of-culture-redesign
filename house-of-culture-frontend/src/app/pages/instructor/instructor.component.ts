import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatDividerModule} from "@angular/material/divider";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatTabsModule} from "@angular/material/tabs";
import {
    ActivitiesCalendarComponent
} from "../../core/components/calendar/activities-calendar/activities-calendar.component";
import {MyActivitiesListComponent} from "../activities/my-activities/my-activities-list/my-activities-list.component";
import {BaseComponent} from "../../core/abstract-base/base.component";
import {ActivitySummaryResponseTs, SingleEventOccurenceTs} from "../../../../out/api";
import {CalendarDateHelperService} from "../../core/components/calendar/helper/calendar-date-helper.service";
import {LoadingService} from "../../core/services/loading.service";
import {takeUntil} from "rxjs";
import {formatDateToLocaleISOString, getMonthEvents} from "../../core/util/date-utils";
import {InstructorService} from "./service/instructor.service";
import {CalendarEvent} from "angular-calendar";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {SingleEventActionDialogComponent} from "./single-event-action-dialog/single-event-action-dialog.component";
import {
    ActivityCardComponent
} from "../activities/my-activities/my-activities-list/activity-card/activity-card.component";
import {MatCardModule} from "@angular/material/card";
import {SingleActivityCardComponent} from "./single-activity-card/single-activity-card.component";
import {
    CalendarExportDialogComponent
} from "../../core/components/calendar-export-dialog/calendar-export-dialog.component";
import {MatIconModule} from "@angular/material/icon";
import {MatButton, MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-instructor',
  standalone: true,
  imports: [CommonModule, MatCheckboxModule, MatDividerModule, MatFormFieldModule, MatTabsModule, ActivitiesCalendarComponent, MyActivitiesListComponent, MatDialogModule, ActivityCardComponent, MatCardModule, SingleActivityCardComponent, MatIconModule, MatButtonModule],
  templateUrl: './instructor.component.html',
  styleUrls: ['./instructor.component.scss']
})
export class InstructorComponent extends BaseComponent implements OnInit {
  activity: ActivitySummaryResponseTs[] = []
  singleEvents: SingleEventOccurenceTs[] = [];
  private prev_month = new Date().getMonth();
  isScrollable: boolean = false;
  constructor(
    private instructorService: InstructorService,
    private dateHelperService: CalendarDateHelperService,
    private loadingService: LoadingService,
    public dialog: MatDialog
  ) {
    super();
  }

  ngOnInit(): void {
    this.onDateUpdated(new Date());

    this.instructorService.getInstructorActivities().pipe(takeUntil(this.destroyed$)).subscribe(response => {
      this.activity = response;
      this.checkScrollable();
    })

    this.dateHelperService.dateChanged$.pipe(takeUntil(this.destroyed$)).subscribe(date => {
      if (this.shouldUpdateEvents(date)) {
        this.prev_month = date.getMonth();
        this.onDateUpdated(date);
      }
    });
  }

  private shouldUpdateEvents(date: Date): boolean {
    return date.getMonth() !== this.prev_month;
  }

  private onDateUpdated(date: Date): void {

    const {start, end} = getMonthEvents(date);
    const startDate = formatDateToLocaleISOString(start);
    const endDate = formatDateToLocaleISOString(end);

    this.loadingService.setLoadingTrue()
    this.instructorService.getInstructorSingleEvents({startDate: startDate, endDate: endDate}).subscribe(response => {
      this.singleEvents = response;
      this.loadingService.setLoadingFalse()
    });
  }

  eventClicked(event: {event: CalendarEvent}): void {
    if(!event.event.meta.id) return;
    this.dialog.open(SingleEventActionDialogComponent, {
      data: event.event.meta
    })
  }

  getCalendar() {
    this.dialog.open(CalendarExportDialogComponent, {
      data: {
        instructorCalendar: true,
        children: []
      }
    })
  }

  private checkScrollable() {
    this.isScrollable = this.activity?.length > 3;
  }
}

