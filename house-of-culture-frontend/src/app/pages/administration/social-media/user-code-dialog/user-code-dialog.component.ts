import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Clipboard} from "@angular/cdk/clipboard";
import {MessageService} from "../../../../core/services/message.service";
import {NotificationMessageType} from "../../../../core/models/notification-message";

export interface UserCodeDialogData {
  code: string;
  verificationUri: string;
}

@Component({
  selector: 'app-user-code-dialog',
  templateUrl: './user-code-dialog.component.html',
  styleUrls: ['./user-code-dialog.component.scss']
})
export class UserCodeDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UserCodeDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserCodeDialogData,
    private clipboard: Clipboard,
    private messageService: MessageService
  ) {}

  closeDialog() {
    this.dialogRef.close()
  }

  copyUserCodeToClipboard() {
    this.clipboard.copy(this.data.code)
    this.messageService.sendMessage("Skopiowano kod!", NotificationMessageType.SUCCESS)
  }
}
