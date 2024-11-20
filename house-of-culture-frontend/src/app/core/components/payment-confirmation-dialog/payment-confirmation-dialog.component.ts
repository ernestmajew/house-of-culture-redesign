import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

interface PaymentConfirmationDialogData {
  title: string
  content: string[]
}

@Component({
  selector: 'app-payment-confirmation-dialog',
  templateUrl: './payment-confirmation-dialog.component.html',
  styleUrls: ['./payment-confirmation-dialog.component.scss']
})
export class PaymentConfirmationDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<PaymentConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PaymentConfirmationDialogData,
  ) {}

  cancelPayment() {
    this.dialogRef.close(false)
  }

  confirmPayment() {
    this.dialogRef.close(true)
  }
}
