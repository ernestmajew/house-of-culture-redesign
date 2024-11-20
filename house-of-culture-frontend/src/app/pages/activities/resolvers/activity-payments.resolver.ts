import {ResolveFn, Router} from '@angular/router';
import {EnrollmentPaymentService} from "../services/enrollment-payment.service";
import {inject} from "@angular/core";
import {catchError, EMPTY} from "rxjs";
import {EnrollmentPaymentsResponseTs} from 'out/api/model/enrollment-payments-response';
import {MessageService} from "../../../core/services/message.service";
import {NotificationMessageType} from "../../../core/models/notification-message";

export const activityPaymentsResolver: ResolveFn<EnrollmentPaymentsResponseTs> = (route, state) => {
  // cannot inject them inside catchError
  const router = inject(Router)
  const messageService = inject(MessageService)

  const eventId = route.params['eventId']
  const userId = route.params['userId']

  return inject(EnrollmentPaymentService)
    .getEnrollmentsPayments(eventId, userId)
    .pipe(catchError((err) => {
      let message = "Nie udało się załadować płatności na dane zajęcia."

      if(err.status == 403) {
        message = "Nie można załadować płatności podanego użytkownika."
      } else if (err.status == 404) {
        message = "Podane zajęcia nie istnieją."
      } else if(err.status == 409) {
        message = "Użytkownik nie jest zapisany na podane zajęcia."
      }

      messageService.sendMessage(message, NotificationMessageType.ERROR)
      router.navigateByUrl("/my-activities")
      return EMPTY
    }));
};
