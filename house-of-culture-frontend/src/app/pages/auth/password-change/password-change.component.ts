import {Component, Input, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from "@angular/router";
import {PasswordChangeInfoTs} from "../../../../../out/api";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {CodeInputModule} from "angular-code-input";
import {MessageService} from "../../../core/services/message.service";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {CountdownComponent, CountdownConfig, CountdownEvent, CountdownGlobalConfig} from "ngx-countdown";
import {catchError, config, take} from "rxjs";
import {PasswordChangeService} from "./service/password-change.service";
import {PasswordInputsComponent} from "./password-inputs/password-inputs.component";

function countdownConfigFactory(): CountdownConfig {
  return {format: `mm:ss`};
}

@Component({
  selector: 'app-password-change',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, CodeInputModule, CountdownComponent, PasswordInputsComponent],
  providers: [
    {provide: CountdownGlobalConfig, useFactory: countdownConfigFactory}
  ],
  templateUrl: './password-change.component.html',
  styleUrls: ['./password-change.component.scss']
})
export class PasswordChangeComponent implements OnInit {
  @Input() uuid: string = "";
  passwordChangeInfo?: PasswordChangeInfoTs;
  config: CountdownConfig = {format: `mm:ss`};
  code: string = "";
  changePasswordPage = false;

  constructor(
    private router: Router,
    private messageService: MessageService,
    private changePasswordService: PasswordChangeService
  ) {
    this.refreshPasswordChangeInfo();
  }

  ngOnInit() {
    if (!this.passwordChangeInfo) {
      this.changePasswordService.getPasswordChangeInfo(this.uuid).pipe(take(1))
        .subscribe((response: PasswordChangeInfoTs) => {
          this.passwordChangeInfo = response;
          this.countDownConfig();
        });
    }
  }

  onCodeCompleted(code: string) {
    this.code = code;
    this.validateChangePasswordCode();
  }

  handleEvent(event: CountdownEvent) {
    if (event.status == 3 && this.passwordChangeInfo) {
      this.messageService.sendMessage("Kod weryfikacyjny wygasł odśwież go", NotificationMessageType.ERROR);
    }
  }

  passwordChangeRequest() {
    this.changePasswordService.changePasswordRequest({email: this.passwordChangeInfo!.email})
      .pipe(
        catchError((error) => {
            this.messageService.sendMessage("Nie udało się spróbuj ponownie później", NotificationMessageType.ERROR);
            return error;
          }
        )).subscribe((response: PasswordChangeInfoTs) => {
      this.router.navigate(['/password-change/' + response.uuid], {state: response});
      this.refreshPasswordChangeInfo();
    })
  }

  validateChangePasswordCode() {
    if (+this.code % 97 != 1) {
      this.messageService.sendMessage("Kod weryfikacyjny jest niepoprawny", NotificationMessageType.ERROR);
    } else {
      this.changePasswordService.validateChangePasswordCode(this.passwordChangeInfo!.uuid, this.code)
        .pipe(
          catchError((error) => {
              this.messageService.sendMessage("Kod weryfikacyjny jest niepoprawny", NotificationMessageType.ERROR);
              return error;
            }
          )).subscribe((_ => {
        this.changePasswordPage = true;
      }))
    }
  }

  private refreshPasswordChangeInfo() {
    this.passwordChangeInfo = this.router.getCurrentNavigation()?.extras.state as PasswordChangeInfoTs;
    this.countDownConfig();
  }

  private countDownConfig() {
    if (this.passwordChangeInfo) {
      const expirationDate = new Date(this.passwordChangeInfo.expirationDate);
      this.config = {stopTime: expirationDate.getTime(), format: `mm:ss`};
    }
  }
}
