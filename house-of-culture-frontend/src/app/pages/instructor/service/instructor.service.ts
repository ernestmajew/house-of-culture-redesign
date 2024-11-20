import {Injectable} from '@angular/core';
import {
  ActivityApiService,
  EnrollmentApiService,
  GetInstructorSingleEventsRequestParams,
  SendEmailRequestTs,
  SingleEventApiService,
  SingleEventOccurenceTs
} from "../../../../../out/api";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class InstructorService {

  constructor(
    private singleEventService: SingleEventApiService,
    private enrolmentService: EnrollmentApiService,
    private eventService: ActivityApiService
  ) { }

  getInstructorSingleEvents(requestParams: GetInstructorSingleEventsRequestParams): Observable<SingleEventOccurenceTs[]> {
    return this.singleEventService.getInstructorSingleEvents(requestParams);
  }

  sendMailToEnrolledUsers(singleEventId: number, emailData: SendEmailRequestTs): Observable<void>{
    return this.singleEventService.sendEmailToEnrolledUsers({
      singleEventId: singleEventId,
      sendEmailRequestTs: emailData
    });
  }

  getInstructorActivities() {
    return this.eventService.getInstructorActivity();
  }

  getEnrolledUser(eventId: number, takeFromPast: boolean) {
    return this.enrolmentService.getAllParticipantsOfActivity({eventId: eventId, takeFromPast: takeFromPast});
  }

  deleteUserFromActivities(eventId: number, userId: number) {
    return this.enrolmentService.deleteUserEnrollmentForEvent({eventId: eventId, userId: userId})
  }
}
