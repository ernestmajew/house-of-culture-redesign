import {Component, Inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {UserTs} from "../../../../../out/api";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {MAT_DATE_LOCALE, MatNativeDateModule} from "@angular/material/core";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";


export interface UpdateUserData {
  firstName: string;
  lastName: string;
  birthdate: any;
  phoneNumber: string;
  getsNewsletter: boolean;
}

@Component({
  selector: 'app-edit-account',
  standalone: true,
  imports: [CommonModule, MatDialogModule, FormsModule, MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, ReactiveFormsModule, MatSlideToggleModule, MatDatepickerModule, MatNativeDateModule],
  templateUrl: './edit-account-dialog.component.html',
  styleUrls: ['./edit-account-dialog.component.scss']
})
export class EditAccountDialogComponent implements OnInit {
  userForm: FormGroup = new FormGroup({
      firstName: new FormControl('', Validators.required),
      lastName: new FormControl('', Validators.required),
      birthdate: new FormControl('', Validators.required),
      phoneNumber: new FormControl(''),
      getsNewsletter: new FormControl(false, Validators.required),
    }
  );
  dateFilter = (date: Date | null): boolean => {
    const day = (date || new Date());
    const currentDate = new Date();
    return day <= currentDate;
  };
  constructor(
    public dialogRef: MatDialogRef<EditAccountDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserTs,
  ) {

  }
  ngOnInit(): void {
    this.userForm.patchValue(this.data);
  }


  onNoDialog() {
    this.dialogRef.close(undefined);
  }

  saveData() {
    this.dialogRef.close(this.userForm.value as UpdateUserData);
  }
}
