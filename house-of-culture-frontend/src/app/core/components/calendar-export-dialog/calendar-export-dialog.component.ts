import {Component, Inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {UserInfoTs} from "../../../../../out/api";
import {SingleEventService} from "../../../pages/activities/services/single-event.service";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatIconModule} from "@angular/material/icon";
import {MatSelectModule} from "@angular/material/select";
import {FullnamePipe} from "../../pipes/fullname.pipe";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {formatDateToLocaleISOString} from "../../util/date-utils";
import {catchError} from "rxjs";
import {NotificationMessageType} from "../../models/notification-message";
import {MessageService} from "../../services/message.service";


export interface ExportCalendarDialogData {
  instructorCalendar: boolean;
  children: UserInfoTs[]
}

@Component({
  selector: 'app-calendar-export-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatDatepickerModule, MatIconModule, MatSelectModule, FullnamePipe, ReactiveFormsModule],
  templateUrl: './calendar-export-dialog.component.html',
  styleUrls: ['./calendar-export-dialog.component.scss']
})
export class CalendarExportDialogComponent {
  exportCalendarForm: FormGroup = this.fb.group({
    userId: [-1, Validators.required],
    startDate: [new Date(), Validators.required],
    endDate: [this.calculateEndDate(new Date()), Validators.required],
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CalendarExportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ExportCalendarDialogData,
    private singleEventService: SingleEventService,
    private messageService: MessageService
  ) {
  }


  downloadCalendar() {
    // @ts-ignore
    this.singleEventService.getICSCalendar({
      startDate: formatDateToLocaleISOString(this.exportCalendarForm.value.startDate),
      endDate: formatDateToLocaleISOString(this.addOneDay(this.exportCalendarForm.value.endDate)),
      instructorCalendar: this.data.instructorCalendar,
      userId: this.exportCalendarForm.value.userId === -1 ? undefined : this.exportCalendarForm.value.userId,
    }).subscribe({
      next: (response) => {
        const blob = new Blob([response], { type: 'text/calendar' });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
        this.dialogRef.close();
      },
      error: (err) => {
        if (err.status == 422) {
          this.messageService.sendMessage("Nie znaleziono zajęć w podanym zakresie dat.", NotificationMessageType.ERROR)
        } else {
          this.messageService.sendMessage("Wystąpił błąd podczas tworzenia kalendarza.", NotificationMessageType.ERROR)
        }
        this.dialogRef.close();
      }
    });
  }

  private addOneDay(date: Date): Date {
    const endDate = new Date(date);
    endDate.setDate(endDate.getDate() + 1);
    return endDate;
  }

  private calculateEndDate(startDate: Date): Date {
    const endDate = new Date(startDate);
    endDate.setMonth(endDate.getMonth() + 6);
    return endDate;
  }
}
