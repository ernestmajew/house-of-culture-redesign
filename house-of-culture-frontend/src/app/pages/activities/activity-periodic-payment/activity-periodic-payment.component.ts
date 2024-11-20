import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserInfoTs} from '../../../../../out/api/model/user-info';
import {EnrollmentPaymentService} from "../services/enrollment-payment.service";
import {formatDate} from "@angular/common";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {PeriodicPaymentInvoiceTs} from "../../../../../out/api/model/periodic-payment-invoice";
import {
  PeriodicPaymentInvoiceItemResponseTs
} from "../../../../../out/api/model/periodic-payment-invoice-item-response";
import {MatDialog} from "@angular/material/dialog";
import {
  PaymentConfirmationDialogComponent
} from "../../../core/components/payment-confirmation-dialog/payment-confirmation-dialog.component";
import {RedirectUriResponseTs} from "../../../../../out/api/model/redirect-uri-response";

// interface to work with p-multiSelect
interface UserInfoTsWithLabel extends UserInfoTs{
  displayedLabel: string
}

@Component({
  selector: 'app-activity-periodic-payment',
  templateUrl: './activity-periodic-payment.component.html',
  styleUrls: ['./activity-periodic-payment.component.scss'],
})
export class ActivityPeriodicPaymentComponent extends BaseComponent implements OnInit{
  availableUsersToPayFor: UserInfoTsWithLabel[] = [];
  paymentForm: FormGroup = this.fb.group({
    start: [new Date(Date.now()), [Validators.required]],
    end: [this.getNowDatePlusMonth(), [Validators.required]],
    selectedUsers: [[], [Validators.required, Validators.minLength(1)]],
  }, { validator: this.validateRange });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private enrollmentPaymentService: EnrollmentPaymentService,
    private dialog: MatDialog,
  ) {
    super()
  }

  ngOnInit(): void {
    this.route.data.subscribe(({ loggedInUser, children }) => {
      const fetchedUsers = [loggedInUser].concat(children)
      this.availableUsersToPayFor = fetchedUsers.map((user: UserInfoTs) => ({
        ...user,
        displayedLabel: `${user.firstName} ${user.lastName}`
      }))
    });
  }

  createPaymentInvoice(): void {
    this.enrollmentPaymentService.getPeriodicPaymentInvoice(
      this.formatDate(this.paymentForm.controls['start'].value),
      this.formatDate(this.paymentForm.controls['end'].value),
      this.paymentForm.controls['selectedUsers'].value
    ).subscribe({
      next: (response: PeriodicPaymentInvoiceTs) => {
        if(response.payments.length == 0) {
          this.sendMessage(
            "Wszystkie terminy w podanym okresie zostały już opłacone.",
            NotificationMessageType.INFO
          )
          return
        }

        this.dialog.open(PaymentConfirmationDialogComponent, {
          data: {
            title: `Rachunek za płatność okresową`,
            content: this.buildConfirmationDialogContent(response.payments)
          }
        }).afterClosed().subscribe((confirm) => {
          if(confirm) this.makePayment()
        })
      },
      error: (_) => {
        this.sendMessage("Nie udało się wygenerować podsumowania płatności za podany okres.", NotificationMessageType.ERROR)
      }
    })
  }

  validateRange(control: AbstractControl): { [key: string]: boolean } | null {
    const start = control.get('start')?.value;
    const end = control.get('end')?.value;

    if (start && end && start > end) {
      return { 'endBeforeStart': true };
    }

    return null;
  }

  private makePayment() {
    this.enrollmentPaymentService.createPeriodicPayment(
      this.formatDate(this.paymentForm.controls['start'].value),
      this.formatDate(this.paymentForm.controls['end'].value),
      this.paymentForm.controls['selectedUsers'].value
    ).subscribe({
      next: (response: RedirectUriResponseTs) => {
        window.location.href = response.uri
      },
      error: (err) => {
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

  private buildConfirmationDialogContent(payments: PeriodicPaymentInvoiceItemResponseTs[]) {
    let content = payments.map(p =>
      `${p.event.title} - ${p.user.firstName} ${p.user.lastName}
      (terminów: ${p.unpaidEnrollments}, łączny koszt: ${p.event.cost!!*p.unpaidEnrollments} zł)`
    )
    const totalCost = payments.reduce(
      (acc, p) => acc + p.event.cost!! * p.unpaidEnrollments,
      0
    )
    content.push(`Łączny koszt: ${totalCost} zł`)
    return content
  }

  private formatDate(date: Date) {
    return formatDate(date, "YYYY-MM-dd", "pl")
  }

  private getNowDatePlusMonth() {
    const date = new Date(Date.now())
    date.setMonth(date.getMonth() + 1);
    return date
  }
}
