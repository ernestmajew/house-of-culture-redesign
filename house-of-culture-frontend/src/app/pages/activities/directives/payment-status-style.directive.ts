import {Directive, ElementRef, Input, Renderer2} from "@angular/core";
import {PaidEnrolmentInfoTsStatusEnum} from "../../../../../out/api/model/paid-enrolment-info";

@Directive({
  selector: '[appPaymentStatusStyle]'
})
export class PaymentStatusStyleDirective {
  @Input() set paymentStatus(paymentStatus: PaidEnrolmentInfoTsStatusEnum) {
    this.setStyle(paymentStatus);
  }

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  private setStyle(paymentStatus: PaidEnrolmentInfoTsStatusEnum): void {
    let textColor = '';

    switch (paymentStatus) {
      case PaidEnrolmentInfoTsStatusEnum.CANCELED:
        textColor = '#c62828';
        break;
      case PaidEnrolmentInfoTsStatusEnum.PENDING:
        textColor = '#bd8618';
        break;
      case PaidEnrolmentInfoTsStatusEnum.COMPLETED:
        textColor = '#2e7d32';
        break;
      case PaidEnrolmentInfoTsStatusEnum.NEW:
        textColor = '#006965';
        break;
      default:
        textColor = 'black';
        break;
    }

    this.renderer.setStyle(this.el.nativeElement, 'color', textColor);
  }
}
