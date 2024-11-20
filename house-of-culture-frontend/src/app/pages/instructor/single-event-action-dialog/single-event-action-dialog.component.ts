import {Component, Inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatButtonModule} from "@angular/material/button";
import {SingleEventOccurenceTs} from "../../../../../out/api";
import {MatIconModule} from "@angular/material/icon";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatInputModule} from "@angular/material/input";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {InstructorService} from "../service/instructor.service";
import {MessageService} from "../../../core/services/message.service";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {MatTooltipModule} from "@angular/material/tooltip";

@Component({
  selector: 'app-single-event-action-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule, MatExpansionModule, MatInputModule, ReactiveFormsModule, MatTooltipModule],
  templateUrl: './single-event-action-dialog.component.html',
  styleUrls: ['./single-event-action-dialog.component.scss']
})
export class SingleEventActionDialogComponent {
  panelOpenState = false;
  emailForm: FormGroup = this.fb.group({
    subject: ['', Validators.required],
    content: ['', Validators.required]
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<SingleEventActionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public singleEvent: SingleEventOccurenceTs,
    private instructorService: InstructorService,
    private messageService: MessageService
  ) {
  }

  sendEmail() {
    const subject = this.emailForm.value.subject;
    const content = this.emailForm.value.content;

    this.instructorService.sendMailToEnrolledUsers(this.singleEvent.id, {subject, content})
      .subscribe(() => {
        this.dialogRef.close();
        this.messageService.sendMessage('Wysłano wiadomość do uczestników wydarzenia.', NotificationMessageType.SUCCESS);
      });
  }

}
