import { Injectable } from '@angular/core';
import {Observable, Subject} from "rxjs";


@Injectable({
  providedIn: 'root'
})
export class CalendarDateHelperService {
  private dateChangedSubject: Subject<Date> = new Subject<Date>();

  dateChanged$: Observable<Date> = this.dateChangedSubject.asObservable()
  constructor() { }

  dateChanged(date: Date) {
    this.dateChangedSubject.next(date);
  }
}
