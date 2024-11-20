import { ResolveFn } from '@angular/router';
import {inject} from "@angular/core";
import {ActivitiesService} from "../services/activities.service";
import {FilteredActivitiesResponseTs} from "../../../../../out/api";
import {DomSanitizer} from "@angular/platform-browser";

export const ITEM_PER_PAGE = 12;
export const activitiesResolver: ResolveFn<FilteredActivitiesResponseTs> = (route, state) => {
  const category = route.queryParams['category'];
  const page = +route.queryParams['page'] - 1 || 0;
  let searchText = route.queryParams['searchText'];
  if(searchText) {
    searchText = inject(DomSanitizer).sanitize(0, searchText)
  }

  return inject(ActivitiesService).getActivities(category, page, ITEM_PER_PAGE, searchText)
};
