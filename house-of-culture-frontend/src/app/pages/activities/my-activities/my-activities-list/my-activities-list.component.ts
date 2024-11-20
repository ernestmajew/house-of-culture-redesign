import {Component, EventEmitter, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {CommonModule} from "@angular/common";
import {AngularMaterialModule} from "../../../../core/modules/angular-material.module";
import {ActivityForUserTs} from "../../../../../../out/api";
import {ActivityCardComponent} from "./activity-card/activity-card.component";
import {EnrollmentService} from "../../services/enrollment.service";
import {take} from "rxjs/operators";
import {NotificationMessageType} from "../../../../core/models/notification-message";
import {FullnamePipe} from "../../../../core/pipes/fullname.pipe";
import {BaseComponent} from "../../../../core/abstract-base/base.component";

@Component({
  selector: 'app-my-activities-list',
  templateUrl: './my-activities-list.component.html',
  styleUrls: ['./my-activities-list.component.scss'],
  standalone: true,
  imports: [CommonModule, AngularMaterialModule, ActivityCardComponent],
})
export class MyActivitiesListComponent extends BaseComponent implements OnInit, OnChanges {
  _enrolledActivities: ActivityForUserTs[] = [];

  isScrollable: boolean = false;
  showEventsFromPast: boolean = false;

  @Output() eventDelete: EventEmitter<undefined> = new EventEmitter<undefined>()

  constructor(private enrollmentService: EnrollmentService,
              private fullnamePipe: FullnamePipe) {
    super();
  }

  ngOnInit(): void {
    this.getEnrolledActivities();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.checkScrollable();
  }

  set enrolledActivities(value: any[]) {
    this._enrolledActivities = value;
    this.checkScrollable();
  }

  get enrolledActivities(): any[] {
    return this._enrolledActivities;
  }

  public changeCheckbox(newValue: boolean) {
    this.showEventsFromPast = newValue
    this.getEnrolledActivities();
    this.checkScrollable();
  }

  resign(data: { activity: ActivityForUserTs, numberOfActivities: number }) {
    this.enrollmentService.deleteUserEnrolmentForActivity(data.activity.activity.id, data.activity.user.id, data.numberOfActivities)
      .pipe(take(1))
      .subscribe(_ => {
        const message = `Wypisano użytkownika ${this.fullnamePipe.transform(data.activity.user)} z ${data.numberOfActivities} ostatnich zajęć ${data.activity.activity.title}`;
        this.messageService.sendMessage(message, NotificationMessageType.INFO)
        this.getEnrolledActivities()
        this.eventDelete.emit();
      })
  }

  private getEnrolledActivities() {
    this.enrollmentService.getEnrolledActivities(this.showEventsFromPast).subscribe(response => {
      this.enrolledActivities = response.activities;
    })
  }

  private checkScrollable() {
    this.isScrollable = this.enrolledActivities?.length > 3;
  }
}
