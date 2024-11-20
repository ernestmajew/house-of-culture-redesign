import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AngularMaterialModule} from "../../../core/modules/angular-material.module";
import {MatCardModule} from "@angular/material/card";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../services/auth.service";
import {JwtTokenService} from "../services/jwt-token.service";
import {catchError} from "rxjs";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {MessageService} from "../../../core/services/message.service";
import {AuthResponseTs, PasswordChangeInfoTs} from "../../../../../out/api";
import {Router} from "@angular/router";
import {EditAccountDialogComponent} from "../../my-account/edit-account/edit-account-dialog.component";
import {EmailDialogComponent} from "./email-dialog/email-dialog.component";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {PasswordChangeService} from "../password-change/service/password-change.service";


@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, AngularMaterialModule, MatCardModule, ReactiveFormsModule, MatDialogModule],
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent implements OnInit {

  form: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
  });

  errorMessage = 'Niepoprawny login lub hasło';
  error = false;

  constructor(private authService: AuthService,
              private jwtTokenService: JwtTokenService,
              private messageService: MessageService,
              private router: Router,
              private dialog: MatDialog,
              private passwordChangeService: PasswordChangeService
  ) {
  }

  ngOnInit() {
  }

  login() {
    this.error = false;
    if (this.form.valid) {
      this.authService.login({
        login: this.form.value.email,
        password: this.form.value.password
      }).pipe(
        catchError((error) => {
          this.error = true;
          return error;
        })
      ).subscribe((data: AuthResponseTs) => {
        this.jwtTokenService.setTokens(data.token, data.refreshToken);
        this.router.navigate(['/']);
        this.messageService.sendMessage("Zalogowano pomyślnie.", NotificationMessageType.SUCCESS);
      });
    }
  }

  toRegisterPage() {
    this.router.navigate(['/register']);
  }

  toEmailInput() {
    const dialogRef = this.dialog.open(EmailDialogComponent);

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.passwordChangeRequest(result)
      }
    });
  }

  private passwordChangeRequest(email: string) {
    this.passwordChangeService.changePasswordRequest({email: email})
      .pipe(
        catchError((error) => {
            if (error.status === 404) {
              this.messageService.sendMessage("Nie znaleziono użytkownika o podanym adresie email", NotificationMessageType.ERROR);
            } else {
              this.messageService.sendMessage("Nie udało się spróbuj ponownie później", NotificationMessageType.ERROR);
            }
            return error;
          }
        )).subscribe((response: PasswordChangeInfoTs) => {
      this.router.navigate(['/password-change/' + response.uuid], {state: response});
    })
  }
}

