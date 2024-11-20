import {Injectable} from '@angular/core';
import {EditSingleEventRequestTs, GetICSCalendarRequestParams, SingleEventApiService} from "../../../../../out/api";
import {EditedEvent} from "../../../core/components/edit-single-event-popup/edit-single-event-popup.component";
import {formatDateAndTime} from "../../../core/util/date-utils";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SingleEventService {

  constructor(private service: SingleEventApiService) {
  }

  updateSingleEvent(request: EditedEvent) {
    return this.service.editSingleEvent(
      {
        editSingleEventRequestTs:
          {
            id: request.id,
            startTime: formatDateAndTime(request.startDate, request.startTime),
            endTime: formatDateAndTime(request.startDate, request.endTime),
            isCanceled: request.isCanceled
          } as EditSingleEventRequestTs
      }
    );
  }

  getICSCalendar(requestParameters: GetICSCalendarRequestParams): Observable<Blob> {
    return this.service.getICSCalendar(requestParameters);
  }
}
