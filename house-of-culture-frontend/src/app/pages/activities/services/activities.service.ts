import {Injectable} from '@angular/core';
import {ActivityApiService, CreateActivityRequestTs, EditActivityRequestTs} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class ActivitiesService {

  constructor(private service: ActivityApiService) { }

  getPopularActivities() {
    return this.service.getPopularActivities();
  }

  getActivitiesCategories(numberOfCategories?: number) {
    return this.service.getActivitiesCategories({number: numberOfCategories});
  }

  getActivities(categoryId?: number, page?: number, pageSize?: number, searchText?: string) {
    return this.service.getActivities({category: categoryId, page: page, pageSize: pageSize, text: searchText});
  }

  createActivities(createRequest: CreateActivityRequestTs){
    return this.service.createActivity({createActivityRequestTs: createRequest});
  }

  editActivities(id: number, createRequest: EditActivityRequestTs){
    return this.service.editActivity({id: id, editActivityRequestTs: createRequest});
  }

  getActivityById(activityId: number){
    return this.service.getActivityById({id: activityId})
  }

  deleteActivityById(id: number) {
    return this.service.deleteActivity({id: id})
  }
}
