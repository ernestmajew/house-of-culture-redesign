import {Injectable} from '@angular/core';
import {EnrollmentApiService} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class UserActivityService {

  constructor(private service: EnrollmentApiService) {
  }

  getUserActivities() {
    return this.service.getAllActivitiesEnrollmentForUser({showEventsFromPast: false})
  }
}
