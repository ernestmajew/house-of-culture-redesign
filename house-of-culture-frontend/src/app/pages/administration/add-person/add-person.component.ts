import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {RegisterComponent} from "../../auth/register/register.component";
import {ActivatedRoute, Router} from "@angular/router";
import {CreateUserFormComponent} from "../../../core/components/create-user-form/create-user-form.component";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../my-account/service/user.service";
import {MessageService} from "../../../core/services/message.service";
import {catchError} from "rxjs";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {AuthResponseTs, CreateUserRequestTs, UserRoleTs} from "../../../../../out/api";
import {formatDateToLocaleISOString} from "../../../core/util/date-utils";
import {SharedComponentsModule} from "../../../core/components/shared-components.module";

@Component({
  selector: 'app-add-person',
  standalone: true,
    imports: [CommonModule, MatButtonModule, MatIconModule, RegisterComponent, CreateUserFormComponent, SharedComponentsModule],
  templateUrl: './add-person.component.html',
  styleUrls: ['./add-person.component.scss']
})
export class AddPersonComponent {
  userForm: FormGroup = new FormGroup({
      name: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      phoneNumber: new FormControl(null, Validators.pattern('(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)')),
      dateOfBirth: new FormControl('', Validators.required),
      role: new FormControl(UserRoleTs.EMPLOYEE, Validators.required)
    }
  );

  constructor(
    private userService: UserService,
    private messageService: MessageService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
  }

  addUser() {
    if (this.userForm.valid) {
      const request = this.getCreateUserRequest();

      this.userService.createUser(request).pipe(
        catchError((error) => {
          if (error.status === 409) {
            this.messageService.sendMessage("Email jest już zajęty.", NotificationMessageType.ERROR);
          } else if (error.status === 400) {
            this.messageService.sendMessage("Sprawdz pola i spróbuj ponownie", NotificationMessageType.ERROR);
          }
          return error;
        })
      ).subscribe((_) => {
        this.messageService.sendMessage("Dodano pomyślnie.", NotificationMessageType.SUCCESS);
        this.backToPreviousPage()
      });
    }
  }

  backToPreviousPage() {
    this.router.navigate(["../../"], {relativeTo: this.activatedRoute});
  }

  private getCreateUserRequest(): CreateUserRequestTs {
    return {
      name: this.userForm.value.name,
      lastname: this.userForm.value.lastname,
      email: this.userForm.value.email,
      dateOfBirth: formatDateToLocaleISOString(this.userForm.value.dateOfBirth),
      phoneNumber: this.userForm.value.phoneNumber,
      role: this.userForm.value.role
    }
  }
}
