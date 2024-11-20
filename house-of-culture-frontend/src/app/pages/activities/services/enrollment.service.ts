import {Injectable} from '@angular/core';
import {
  EnrollmentApiService,
  GetEnrollmentsRequestParams,
  UserEnrolledSingleEventsResponseTs
} from "../../../../../out/api";
import {Observable} from "rxjs";
import {EnrollmentAvailabilityResponseTs} from "../../../../../out/api/model/enrollment-availability-response";

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {

  constructor(private service: EnrollmentApiService) {
  }

  getEnrolledSingleEvents(requestParameters: GetEnrollmentsRequestParams): Observable<UserEnrolledSingleEventsResponseTs> {
    return this.service.getEnrollments(requestParameters);
  }

  enrollToEvent(eventId: number, numberOfSingleEvents: number, userId?: number) {
    return this.service.enrollToEvent({
      eventId: eventId,
      createEnrollmentRequestTs: {
        numberOfSingleEvents: numberOfSingleEvents,
        userId: userId
      }})
  }

  getEnrollmentAvailability(eventId: number): Observable<EnrollmentAvailabilityResponseTs> {
    return this.service.getEnrollmentAvailability({eventId: eventId})
  }

  getEnrolledActivities(showEventsFromPast: boolean) {
    return this.service.getAllActivitiesEnrollmentForUser({showEventsFromPast: showEventsFromPast})
  }

  deleteUserEnrolmentForActivity(eventId: number, userId?: number, numberOfEventsToDelete?: number) {
    return this.service.deleteUserEnrollmentForActivities({
      eventId: eventId,
      userId: userId,
      numberOfEventsToDelete: numberOfEventsToDelete
    })
  }
}
