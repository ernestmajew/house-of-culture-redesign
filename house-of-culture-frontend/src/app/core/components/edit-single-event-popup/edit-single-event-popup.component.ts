import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {Event} from "../calendar-form/calendar-form.component";
import {AngularMaterialModule} from "../../modules/angular-material.module";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";
import {NgIf} from "@angular/common";

export interface EditedEvent {
  id: number;
  startDate: Date;
  startTime: string;
  endTime: string;
  isCanceled?: boolean;
}

@Component({
  selector: 'app-edit-single-event-popup',
  templateUrl: './edit-single-event-popup.component.html',
  styleUrls: ['./edit-single-event-popup.component.scss'],
  standalone: true,
  imports: [
    AngularMaterialModule,
    FormsModule,
    ReactiveFormsModule,
    NgxMaterialTimepickerModule,
    NgIf
  ]
})
export class EditSingleEventPopupComponent implements OnInit {

  editForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<EditSingleEventPopupComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { event: Event, isEdit: boolean }) {

    const isCanceled = data.event.isCanceled
    let canceledValue = isCanceled ? isCanceled : false
    this.editForm = new FormGroup({
      startDate: new FormControl(data.event.startDate, Validators.required),
      startTime: new FormControl(data.event.startTime, Validators.required),
      endTime: new FormControl(data.event.endTime, Validators.required),
      isCanceled: new FormControl(canceledValue)
    })
  }

  ngOnInit(): void {
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit() {
    const response = {
      id: this.data.event.id,
      startDate: new Date(this.editForm.get("startDate")?.value),
      startTime: this.editForm.get("startTime")?.value,
      endTime: this.editForm.get("endTime")?.value,
      isCanceled: this.editForm.get("isCanceled")?.value
    } as EditedEvent;

    this.dialogRef.close(response);
  }

}
