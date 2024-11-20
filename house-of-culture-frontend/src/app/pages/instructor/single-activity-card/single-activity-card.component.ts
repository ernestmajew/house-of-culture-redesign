import {Component, Input} from '@angular/core';
import {ActivitySummaryResponseTs} from "../../../../../out/api";
import {Router} from "@angular/router";
import {AngularMaterialModule} from "../../../core/modules/angular-material.module";
import {AccessDirective} from "../../../core/guards/directive/access.directive";

@Component({
  selector: 'app-single-activity-card',
  templateUrl: './single-activity-card.component.html',
  styleUrls: ['./single-activity-card.component.scss'],
  standalone: true,
    imports: [
        AngularMaterialModule,
        AccessDirective
    ]
})
export class SingleActivityCardComponent {
  @Input() activity!: ActivitySummaryResponseTs;

  constructor(private router: Router) {
  }


  activityDetail() {
    this.router.navigateByUrl("/activities/" + this.activity!.id)
  }

  enrolledUserView() {
    this.router.navigateByUrl(`/activities/enrollment/instructor/${this.activity?.id}`)
  }
}
