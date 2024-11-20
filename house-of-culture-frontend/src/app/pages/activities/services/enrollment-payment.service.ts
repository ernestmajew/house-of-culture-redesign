import { Injectable } from '@angular/core';
import {EnrollmentPaymentApiService} from "../../../../../out/api/api/enrollment-payment.service";
import {Observable} from "rxjs";
import {EnrollmentPaymentsResponseTs} from "../../../../../out/api/model/enrollment-payments-response";
import {RedirectUriResponseTs} from "../../../../../out/api/model/redirect-uri-response";
import {PeriodicPaymentInvoiceTs} from "../../../../../out/api/model/periodic-payment-invoice";

@Injectable({
  providedIn: 'root'
})
export class EnrollmentPaymentService {
  constructor(
    private service: EnrollmentPaymentApiService
  ) { }

  getEnrollmentsPayments(eventId: number, userId?: number): Observable<EnrollmentPaymentsResponseTs> {
    return this.service.getEnrollmentsPayments({eventId, userId})
  }

  payForEnrollments(eventId: number, userId?: number, numberOfEnrollments?: number): Observable<RedirectUriResponseTs> {
    return this.service.payForEnrollments({eventId, userId, numberOfEnrollments})
  }

  getPeriodicPaymentInvoice(start: string, end: string, usersIds: number[]): Observable<PeriodicPaymentInvoiceTs> {
    return this.service.getPeriodicPaymentInvoice({start, end, usersIds})
  }

  createPeriodicPayment(start: string, end: string, usersIds: number[]): Observable<RedirectUriResponseTs> {
    return this.service.createPeriodicPayment({start, end, usersIds})
  }
}
