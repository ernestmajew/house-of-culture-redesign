import {ResolveFn} from '@angular/router';
import {inject} from "@angular/core";
import {ActivitiesService} from "../services/activities.service";
import {CategoryResponseTs} from "../../../../../out/api";

export const categoryResolver: ResolveFn<CategoryResponseTs[]> = () => {
  return inject(ActivitiesService).getActivitiesCategories()
};
