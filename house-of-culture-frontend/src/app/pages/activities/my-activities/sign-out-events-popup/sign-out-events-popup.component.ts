import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivityForUserTs} from 'out/api';

@Component({
  selector: 'app-sign-out-events-popup',
  templateUrl: './sign-out-events-popup.component.html',
  styleUrls: ['./sign-out-events-popup.component.scss']
})
export class SignOutEventsPopupComponent implements OnInit {
  signOutForm!: FormGroup;
  errorInfo?: String;
  checkboxValue: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<SignOutEventsPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ActivityForUserTs
  ) {
    this.signOutForm = new FormGroup({
      numberOfClasses: new FormControl(1, [Validators.min(1), Validators.max(this.data.numberOfEnrolledSingleEvents)]),
      cancelAllClasses: new FormControl(false),
    });
  }

  ngOnInit(): void {
    this.errorInfo = "Nieprawidłowa wartość, wartość musi być różna od 0";

    this.signOutForm.get("cancelAllClasses")?.valueChanges.subscribe(value => {
      this.checkboxValue = value;
      this.signOutForm.get("numberOfClasses")?.setValue(value ? this.data.numberOfEnrolledSingleEvents : 0)
    })
  }


  confirmCancellation() {
    this.dialogRef.close(
      this.signOutForm.get("cancelAllClasses")?.value
        ? this.data.numberOfEnrolledSingleEvents
        : this.signOutForm.get("numberOfClasses")?.value
    );
  }

  cancel() {
    this.dialogRef.close();
  }

}
