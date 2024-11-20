import {Pipe, PipeTransform} from '@angular/core';
import {PaidEnrolmentInfoTs, PaidEnrolmentInfoTsStatusEnum} from "../../../../../out/api/model/paid-enrolment-info";

@Pipe({
  name: 'paymentStatus'
})
export class PaymentStatusPipe implements PipeTransform {

  transform(payment: PaidEnrolmentInfoTs): string {
    switch (payment.status) {
      case PaidEnrolmentInfoTsStatusEnum.NEW:
        return  "Nowa";
      case PaidEnrolmentInfoTsStatusEnum.CANCELED:
        return "Anulowane";
      case PaidEnrolmentInfoTsStatusEnum.PENDING:
        return "W trakcie przetwarzania";
      case PaidEnrolmentInfoTsStatusEnum.COMPLETED:
        return "Zaksięgowana";
      default:
        return "W trakcie przetwarzania";
    }
  }
}
