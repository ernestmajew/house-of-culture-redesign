import {Component, Input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatIconModule} from "@angular/material/icon";
import {MatInputModule} from "@angular/material/input";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {RegExValidators} from "../../register/validators/RegExValidator";
import {MatCardModule} from "@angular/material/card";
import {MatButtonModule} from "@angular/material/button";
import {Router} from "@angular/router";
import {PasswordChangeService} from "../service/password-change.service";
import {NotificationMessageType} from "../../../../core/models/notification-message";
import {catchError} from "rxjs";
import {PasswordChangeInfoTs} from "../../../../../../out/api";
import {MessageService} from "../../../../core/services/message.service";
import {JwtTokenService} from "../../services/jwt-token.service";

@Component({
  selector: 'app-password-inputs',
  standalone: true,
  imports: [CommonModule, MatDatepickerModule, MatFormFieldModule, MatIconModule, MatInputModule, ReactiveFormsModule, MatCardModule, MatButtonModule],
  templateUrl: './password-inputs.component.html',
  styleUrls: ['./password-inputs.component.scss']
})
export class PasswordInputsComponent {
  @Input() code!: string;
  @Input() passwordChangeInfo?: PasswordChangeInfoTs;
  hide = true;

  passwordForm: FormGroup = new FormGroup({
      password: new FormControl('', [
        Validators.required,
        Validators.minLength(8),
        RegExValidators.patternValidator(new RegExp("(?=.*[0-9])"), {
          requiresDigit: true
        }),
        RegExValidators.patternValidator(new RegExp("(?=.*[A-Z])"), {
          requiresUppercase: true
        }),
        RegExValidators.patternValidator(new RegExp("(?=.*[a-z])"), {
          requiresLowercase: true
        }),
        RegExValidators.patternValidator(new RegExp("(?=.*[!@#$%^&+=])"), {
          requiresSpecialChars: true
        })]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    {
      validators: RegExValidators.MatchPasswordValidator
    }
  );

  constructor(
    private router: Router,
    private passwordChangeService: PasswordChangeService,
    private messageService: MessageService,
    private jwtTokenService: JwtTokenService
  ) {
  }

  changePassword() {
    if (this.passwordForm.valid) {
      this.passwordChangeService.changeUserPassword({
        code: this.code,
        password: this.passwordForm.value.password,
        uuid: this.passwordChangeInfo!.uuid
      })
        .pipe(
          catchError((error) => {
              this.messageService.sendMessage("Zmiana hasła nie powiodła się", NotificationMessageType.ERROR);
              return error;
            }
          ))
        .subscribe(_ => {
          this.jwtTokenService.clearToken();
          this.messageService.sendMessage("Hasło zostało zmienione", NotificationMessageType.SUCCESS);
          this.router.navigate(['/login']);
        })
    }
  }
}
