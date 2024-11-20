import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ActivityForUserTs} from 'out/api';
import {Router} from "@angular/router";
import {AngularMaterialModule} from "../../../../../core/modules/angular-material.module";
import {FullnamePipe} from "../../../../../core/pipes/fullname.pipe";
import {NgClass, NgIf} from "@angular/common";
import {MatDialog} from "@angular/material/dialog";
import {SignOutEventsPopupComponent} from "../../sign-out-events-popup/sign-out-events-popup.component";

@Component({
  selector: 'app-activity-card',
  templateUrl: './activity-card.component.html',
  styleUrls: ['./activity-card.component.scss'],
  standalone: true,
  imports: [AngularMaterialModule, FullnamePipe, NgIf, NgClass]
})
export class ActivityCardComponent implements OnInit {
  @Input() activityInfo!: ActivityForUserTs;
  @Output() resignActivity: EventEmitter<{ activity: ActivityForUserTs, numberOfActivities: number }> =
    new EventEmitter<{ activity: ActivityForUserTs, numberOfActivities: number }>()
  costOfActivityString?: string;
  debtOfUser?: string;

  constructor(private router: Router, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    const cost = this.activityInfo.activity.cost;
    this.costOfActivityString = cost ? cost + "zł / spotkanie" : "darmowe"

    const debt = this.activityInfo.debtForPastEvents;
    this.debtOfUser = (debt === 0) ? "Brak zaległości" : `Zaległość: ${debt}zł`
  }

  activityDetail(){
    this.router.navigateByUrl("/activities/" + this.activityInfo.activity.id)
  }

  resign() {
    const dialogRef = this.dialog.open(SignOutEventsPopupComponent, {
      data: this.activityInfo,
      width: '400px',
      autoFocus: false
    })

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.resignActivity.emit({activity: this.activityInfo, numberOfActivities: result})
      }
    })
  }

  navigateToPayments() {
    this.router.navigateByUrl(`/my-activities/${this.activityInfo.activity.id}/payment/${this.activityInfo.user.id}`)
  }
}
