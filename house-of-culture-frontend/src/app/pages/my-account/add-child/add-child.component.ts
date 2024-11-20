import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {ActivatedRoute, Router} from "@angular/router";
import {UserFormComponent} from "../../auth/register/user-form/user-form.component";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {MessageService} from "../../../core/services/message.service";
import {formatDateToLocaleISOString} from "../../../core/util/date-utils";
import {catchError} from "rxjs";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {CreateUserRequestTs, UserRoleTs} from "../../../../../out/api";
import {RegisterComponent} from "../../auth/register/register.component";
import {CreateUserFormComponent} from "../../../core/components/create-user-form/create-user-form.component";
import {UserService} from "../service/user.service";
import {SharedComponentsModule} from "../../../core/components/shared-components.module";

@Component({
  selector: 'app-add-child',
  standalone: true,
    imports: [CommonModule, MatButtonModule, MatIconModule, UserFormComponent, RegisterComponent, CreateUserFormComponent, SharedComponentsModule],
  templateUrl: './add-child.component.html',
  styleUrls: ['./add-child.component.scss']
})
export class AddChildComponent {
  childForm: FormGroup = new FormGroup({
      name: new FormControl('', Validators.required),
      lastname: new FormControl('', Validators.required),
      email: new FormControl('', [Validators.required, Validators.email]),
      phoneNumber: new FormControl(null, Validators.pattern('(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)')),
      dateOfBirth: new FormControl('', Validators.required),
    }
  );

  constructor(
    private userService: UserService,
    private messageService: MessageService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
  }

  addChild() {
    if (this.childForm.valid) {
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
    this.router.navigate(["../"], {relativeTo: this.activatedRoute});
  }

  private getCreateUserRequest(): CreateUserRequestTs {
    return {
      name: this.childForm.value.name,
      lastname: this.childForm.value.lastname,
      email: this.childForm.value.email,
      dateOfBirth: formatDateToLocaleISOString(this.childForm.value.dateOfBirth),
      phoneNumber: this.childForm.value.phoneNumber,
      role: UserRoleTs.CHILD
    }
  }
}
