import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AccountService} from "./service/account.service";
import {ActivityForUserTs, PasswordChangeInfoTs, UserTs} from "../../../../out/api";
import {MatCardModule} from "@angular/material/card";
import {MatDividerModule} from "@angular/material/divider";
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatDialog, MatDialogModule} from "@angular/material/dialog";
import {EditAccountDialogComponent, UpdateUserData} from "./edit-account/edit-account-dialog.component";
import {MatNativeDateModule} from "@angular/material/core";
import {catchError} from "rxjs";
import {NotificationMessageType} from "../../core/models/notification-message";
import {MessageService} from "../../core/services/message.service";
import {formatDateToLocaleISOString} from "../../core/util/date-utils";
import {PasswordChangeService} from "../auth/password-change/service/password-change.service";
import {Router, RouterOutlet} from "@angular/router";
import {MatMenuModule} from "@angular/material/menu";
import {ConnectedAccountsComponent} from "./connected-accounts/connected-accounts.component";
import {MyActivityListComponent} from "./my-activity-list/my-activity-list.component";
import {UserActivityService} from "./service/user-activity.service";

@Component({
  selector: 'app-my-account',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatDividerModule, MatProgressBarModule, MatButtonModule, MatIconModule, MatDialogModule, MatNativeDateModule, RouterOutlet, MatMenuModule, ConnectedAccountsComponent, MyActivityListComponent],
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.scss']
})
export class MyAccountComponent implements OnInit {
  user?: UserTs
  userActivities: ActivityForUserTs[] = []

  constructor(
    private service: AccountService,
    private userActivityService: UserActivityService,
    public dialog: MatDialog,
    private messageService: MessageService,
    private changePasswordService: PasswordChangeService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.service.getUserInfo().subscribe(
      (user) => {
        this.user = user;
      }
    )
    this.userActivityService.getUserActivities().subscribe(response => {
        this.userActivities = response.activities;
      }
    )
  }


  editAccount() {
    if (!this.user) return;
    const dialogRef = this.dialog.open(EditAccountDialogComponent, {
      data: this.user,
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result){
        this.updateUserInfo(result);
      }
    });
  }

  changePasswordRequest() {
    this.changePasswordService.changePasswordRequest({email: this.user!.email})
      .pipe(
        catchError((error) => {
          this.messageService.sendMessage("Nie udało się spróbuj ponownie później", NotificationMessageType.ERROR);
          return error;
        }
      )).subscribe((response: PasswordChangeInfoTs) => {
        this.router.navigate(['/password-change/' + response.uuid], {state: response});
    })
  }

  private updateUserInfo(updateUserRequest: UpdateUserData) {
    updateUserRequest.birthdate = formatDateToLocaleISOString(updateUserRequest.birthdate);

    this.service.updateUserInfo(updateUserRequest)
      .pipe(
        catchError((error) => {
          if (error.status === 400) {
            this.messageService.sendMessage("Sprawdz pola i spróbuj ponownie edycji", NotificationMessageType.ERROR);
          }
          return error;
        })
      ).subscribe(
      (user) => {
        this.user = user;
      }
    )
  }

  addChild() {
    this.router.navigate(['account/add-child']);
  }
}
