import {Component, OnInit, ViewChild} from '@angular/core';
import {EnrollmentService} from "../services/enrollment.service";
import {SingleEventOccurenceTs, UserInfoTs} from "../../../../../out/api";
import {formatDateToLocaleISOString, getMonthEvents} from "../../../core/util/date-utils";
import {takeUntil} from "rxjs";
import {CalendarDateHelperService} from "../../../core/components/calendar/helper/calendar-date-helper.service";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {UserService} from "../../my-account/service/user.service";
import {MatTabChangeEvent} from "@angular/material/tabs";
import {LoadingService} from "../../../core/services/loading.service";
import {FormControl} from "@angular/forms";
import {MyActivitiesListComponent} from "./my-activities-list/my-activities-list.component";
import {MatDialog} from "@angular/material/dialog";
import {
  CalendarExportDialogComponent
} from "../../../core/components/calendar-export-dialog/calendar-export-dialog.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-my-activities',
  templateUrl: './my-activities.component.html',
  styleUrls: ['./my-activities.component.scss']
})
export class MyActivitiesComponent extends BaseComponent implements OnInit{
  enrolledEvents: SingleEventOccurenceTs[] = [];
  takeActivitiesFromPastControl = new FormControl(false);
  @ViewChild(MyActivitiesListComponent) childComponentRef!: MyActivitiesListComponent;

  user?: UserInfoTs;
  userId?: number;
  children: UserInfoTs[] = [];
  private prev_month = new Date().getMonth();

  constructor(
    private enrollmentService: EnrollmentService,
    private dateHelperService: CalendarDateHelperService,
    private userService: UserService,
    private loadingService: LoadingService,
    public dialog: MatDialog,
    private router: Router
  ) {
    super();
  }

  ngOnInit(): void {
    this.getChildren();
    this.onDateUpdated(new Date());

    this.dateHelperService.dateChanged$.pipe(takeUntil(this.destroyed$)).subscribe(date => {
      if (this.shouldUpdateEvents(date)) {
        this.prev_month = date.getMonth();
        this.onDateUpdated(date);
      }
    });

    this.takeActivitiesFromPastControl.valueChanges.subscribe(value => {
      this.childComponentRef.changeCheckbox(value!);
    })
  }

  onTabChange(event: MatTabChangeEvent) {
    const index = event.index -1;
    if (index === -1) {
      this.userId = undefined;
      return this.onDateUpdated(new Date());
    }else{
      this.userId = this.children[index].id;
      return this.onDateUpdated(new Date());
    }
  }

  onEventDelete() {
    this.onDateUpdated(new Date());
  }

  getCalendar() {
    this.dialog.open(CalendarExportDialogComponent, {
      data: {
        instructorCalendar: false,
        children: this.children
      }
    })
  }

  navigateToPeriodicPayment() {
    this.router.navigateByUrl("my-activities/payment/periodic")
  }

  private getChildren(): void {
    this.userService.getChildren().subscribe(children => {
      this.children = children;
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
    this.enrollmentService.getEnrolledSingleEvents({startDate: startDate, endDate: endDate, userId: this.userId}).subscribe(response => {
      this.enrolledEvents = response.events;
      this.user = response.user;
      this.loadingService.setLoadingFalse()
    });
  }

  //we must take first week of the previous month and last week of the next month
  private getMonthEvents(date: Date): {start: Date, end: Date} {
    const lastDayOfPreviousMonth = new Date(date.getFullYear(), date.getMonth(), 0);
    const firstDayOfNextMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0);

    const firstDay = new Date(lastDayOfPreviousMonth);
    firstDay.setDate(firstDay.getDate() - lastDayOfPreviousMonth.getDay());

    const lastDay = new Date(firstDayOfNextMonth);
    lastDay.setDate(lastDay.getDate() + (7 - firstDayOfNextMonth.getDay()));

    return { start: firstDay, end: lastDay };
  };
}
