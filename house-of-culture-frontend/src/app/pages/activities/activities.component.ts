import {Component, OnInit} from '@angular/core';
import { BaseComponent } from "../../core/abstract-base/base.component";
import {Router} from "@angular/router";
import {
  ActivitySummaryResponseTs,
  CategoryResponseTs,
  PopularActivitiesCategorizedResponseTs,
  PopularActivitiesResponseTs
} from "../../../../out/api";
import {ActivitiesService} from "./services/activities.service";
import {take} from "rxjs/operators";

@Component({
  selector: 'app-activities',
  templateUrl: './activities.component.html',
  styleUrls: ['./activities.component.scss']
})
export class ActivitiesComponent extends BaseComponent implements OnInit {
  activities: ActivitySummaryResponseTs[] = [];
  categoriesMain: CategoryResponseTs[] = [];
  categorizedActivities: PopularActivitiesCategorizedResponseTs[] = [];

  constructor(
    private router: Router,
    private activitiesService: ActivitiesService,
  ) {
    super();
  }

  ngOnInit() {
    this.activitiesService.getPopularActivities().pipe(take(1)).subscribe((activities: PopularActivitiesResponseTs) => {
      this.activities = activities.main;
      this.categorizedActivities = activities.categorized
    });
    this.activitiesService.getActivitiesCategories(3).pipe(take(1)).subscribe(categories => {
      this.categoriesMain = categories.slice(0, 3);
    });
  }

  navigateToCategory(category?: number) {
    if (!category || category.toString() === '') {
      this.router.navigateByUrl('/activities/offers');
    } else {
      this.router.navigateByUrl(`/activities/offers?category=${category}`);
    }
    window.scrollTo(0, 0);
  }
}
