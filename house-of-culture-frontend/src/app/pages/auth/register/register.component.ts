import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatInputModule} from "@angular/material/input";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatButtonModule} from "@angular/material/button";
import {MatNativeDateModule} from "@angular/material/core";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {RegExValidators} from "./validators/RegExValidator";
import {MatIconModule} from "@angular/material/icon";
import {AuthService} from "../services/auth.service";
import {catchError} from "rxjs";
import {AuthResponseTs, RegisterRequestTs} from "../../../../../out/api";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {JwtTokenService} from "../services/jwt-token.service";
import {MessageService} from "../../../core/services/message.service";
import {Router} from "@angular/router";
import {formatDateToLocaleISOString} from "../../../core/util/date-utils";
import {UserFormComponent} from "./user-form/user-form.component";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, MatInputModule, MatDatepickerModule, MatButtonModule, MatNativeDateModule, ReactiveFormsModule, MatIconModule, UserFormComponent],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent{
  registerForm: FormGroup = new FormGroup({
      name: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      phoneNumber: new FormControl(null, Validators.pattern('(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)')),
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
      dateOfBirth: new FormControl('', [Validators.required, ageValidator]),
    },
    {
      validators: RegExValidators.MatchPasswordValidator
    }
  );

  constructor(
    private authService: AuthService,
    private jwtTokenService: JwtTokenService,
    private messageService: MessageService,
    private router: Router
  ) {}


  register() {
    if (this.registerForm.valid) {
      const request = this.getRegisterDataToRequest();

      this.authService.register(request).pipe(
        catchError((error) => {
          if (error.status === 409){
            this.messageService.sendMessage("Email jest już zajęty.", NotificationMessageType.ERROR);
          }else if (error.status === 400) {
            this.messageService.sendMessage("Sprawdz pola i spróbuj ponownie", NotificationMessageType.ERROR);
          }
          return error;
        })
      ).subscribe((data: AuthResponseTs) => {
          this.jwtTokenService.setTokens(data.token, data.refreshToken);
          this.router.navigate(['/']);
          this.messageService.sendMessage("Zarejestrowano pomyślnie.", NotificationMessageType.SUCCESS);
      });
    }
  }

  private getRegisterDataToRequest(): RegisterRequestTs {
    return {
      name: this.registerForm.value.name,
      lastname: this.registerForm.value.lastname,
      email: this.registerForm.value.email,
      password: this.registerForm.value.password,
      dateOfBirth: formatDateToLocaleISOString(this.registerForm.value.dateOfBirth),
      phoneNumber: this.registerForm.value.phoneNumber,
    }
  }

}

const ageValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const birthDate = new Date(control.value);
  const currentDate = new Date();
  const ageDiff = currentDate.getFullYear() - birthDate.getFullYear();

  if (
    ageDiff < 18 ||
    (ageDiff === 18 && birthDate.getMonth() > currentDate.getMonth()) ||
    (ageDiff === 18 && birthDate.getMonth() === currentDate.getMonth() && birthDate.getDate() > currentDate.getDate())
  ) {
    return { notOldEnough: true };
  }

  return null;
};
