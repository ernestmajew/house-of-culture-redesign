import {Component, Input} from '@angular/core';
import {AngularMaterialModule} from "../../../../core/modules/angular-material.module";
import {ActivityForUserTs} from "../../../../../../out/api";
import {FullnamePipe} from "../../../../core/pipes/fullname.pipe";
import {Router} from "@angular/router";

@Component({
  selector: 'app-activity-info-card',
  templateUrl: './activity-info-card.component.html',
  styleUrls: ['./activity-info-card.component.scss'],
  standalone: true,
  imports: [AngularMaterialModule, FullnamePipe]
})
export class ActivityInfoCardComponent {
  @Input() activityInfo!: ActivityForUserTs;

  constructor(private router: Router) {
  }

  activityDetail(){
    this.router.navigateByUrl("/activities/" + this.activityInfo.activity.id)
  }

  payment(){
    console.log("Payment: " + this.activityInfo)
  }
}
