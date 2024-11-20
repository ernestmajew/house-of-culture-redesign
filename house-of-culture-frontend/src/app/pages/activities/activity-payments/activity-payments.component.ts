import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserInfoTs} from "../../../../../out/api/model/user-info";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {MatDialog} from "@angular/material/dialog";
import {PaymentConfirmationDialogComponent} from "../../../core/components/payment-confirmation-dialog/payment-confirmation-dialog.component";
import {PaidEnrolmentInfoTs} from "../../../../../out/api/model/paid-enrolment-info";
import {EventInfoTs} from "../../../../../out/api/model/event-info";
import {EnrollmentPaymentService} from "../services/enrollment-payment.service";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {RedirectUriResponseTs} from "../../../../../out/api/model/redirect-uri-response";

@Component({
  selector: 'app-activity-payments',
  templateUrl: './activity-payments.component.html',
  styleUrls: ['./activity-payments.component.scss']
})
export class ActivityPaymentsComponent extends BaseComponent implements OnInit {
  user!: UserInfoTs
  event!: EventInfoTs
  unpaidEnrollments!: number
  historyOfPayments: PaidEnrolmentInfoTs[] = []

  paymentForm!: FormGroup;
  occurrencesNumberDisabled?: boolean

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private enrollmentPaymentService: EnrollmentPaymentService
  ) {
    super()
  }

  ngOnInit(): void {
    this.route.data.subscribe(({payments}) => {
      this.user = payments.user
      this.event = payments.event
      this.unpaidEnrollments = payments.unpaidEnrollments
      this.historyOfPayments = payments.paidEnrollments

      this.paymentForm = this.formBuilder.group({
        occurrencesNumber: [1, [Validators.required, Validators.min(1), Validators.max(this.unpaidEnrollments)]],
        payForAll: [false, Validators.required]
      });

      this.paymentForm.controls['payForAll'].valueChanges.subscribe((value) => {
        // material input requires to pass undefined/null instead of false (IDK why)
        this.occurrencesNumberDisabled = value ? true : undefined
        if(value) this.paymentForm.controls['occurrencesNumber'].setValue(this.unpaidEnrollments)
      })
    });
  }

  showPaymentDialog() {
    const occurrencesToPay = this.paymentForm.controls['occurrencesNumber'].value
    const totalCost = (occurrencesToPay * this.event.cost!!).toFixed(2)
    this.dialog.open(PaymentConfirmationDialogComponent, {
      data: {
        title: `Potwierdź opłatę za ${this.event.title}`,
        content: [
          `Opłacisz terminów: ${occurrencesToPay}`,
          `Na kwotę: ${totalCost} zł`
        ]
      }
    }).afterClosed().subscribe((confirm) => {
      if(confirm) this.makePayment()
    })
  }

  private makePayment() {
    this.enrollmentPaymentService.payForEnrollments(
      this.event.id,
      this.user.id,
      this.paymentForm.controls['occurrencesNumber'].value
    ).subscribe({
      next: (response: RedirectUriResponseTs) => {
        window.location.href = response.uri
      },
      error: (err) => {
        console.log(err)

        let message!: string
        // client errors
        if(err.status >= 400 && err.status < 500) {
          message = "Płatność nieudana. Niewłaściwe dane w żądaniu płatności."
        }else {
          message = "Płatność nie powiodła się. Spróbuj ponownie później."
        }

        this.sendMessage(message, NotificationMessageType.ERROR)
        this.router.navigateByUrl("/my-activities")
      }
    })
  }
}
