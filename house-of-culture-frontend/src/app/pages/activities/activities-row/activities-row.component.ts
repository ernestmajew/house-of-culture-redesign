import {Component, Input} from '@angular/core';
import {ActivitySummaryResponseTs} from "../../../../../out/api";
import {Router} from "@angular/router";

@Component({
  selector: 'app-activities-row',
  templateUrl: './activities-row.component.html',
  styleUrls: ['./activities-row.component.scss']
})
export class ActivitiesRowComponent {
  @Input() title: string = "";
  @Input() activities: ActivitySummaryResponseTs[] = [];
  @Input() categoryId?: number; // to link

  constructor(
    private router: Router,
  ) {

  }

  goToCategory(categoryId?: number) {
    if (categoryId) {
      this.router.navigateByUrl(`/activities/offers?category=${categoryId}`);
      window.scrollTo(0, 0);
    }
  }
}
