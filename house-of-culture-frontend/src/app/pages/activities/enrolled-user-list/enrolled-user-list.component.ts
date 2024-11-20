import {Component, OnInit} from '@angular/core';
import {InstructorService} from "../../instructor/service/instructor.service";
import {UserEnrolmentWithDebtTs} from "../../../../../out/api";
import {take} from "rxjs/operators";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {AngularMaterialModule} from "../../../core/modules/angular-material.module";
import {FullnamePipe} from "../../../core/pipes/fullname.pipe";
import {CommonModule} from "@angular/common";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../../../core/components/confirmation-dialog/confirmation-dialog.component";
import {SharedComponentsModule} from "../../../core/components/shared-components.module";

@Component({
  selector: 'app-enrolled-user-list',
  templateUrl: './enrolled-user-list.component.html',
  styleUrls: ['./enrolled-user-list.component.scss'],
  standalone: true,
    imports: [
      FullnamePipe,
      ReactiveFormsModule,
      AngularMaterialModule,
      CommonModule,
      SharedComponentsModule
    ]
})
export class EnrolledUserListComponent extends BaseComponent implements OnInit {

  eventId?: number;
  enrolledUser: UserEnrolmentWithDebtTs[] = []
  takeUserEnrolmentFromPastControl = new FormControl(false);

  isInstructor: boolean = false;

  constructor(
    private instructorService: InstructorService,
    private activatedRoute: ActivatedRoute,
    private fullnamePipe: FullnamePipe,
    public dialog: MatDialog
  ) {
    super();
  }

  ngOnInit(): void {
    this.isInstructor = this.activatedRoute.snapshot.data['instructor'];

    this.activatedRoute.params.subscribe(params => {
      this.eventId = params['id']
    })

    this.getEnrolledUser(false);

    this.takeUserEnrolmentFromPastControl.valueChanges.subscribe(value => {
      this.getEnrolledUser(value!)
    })
  }

  getEnrolledUser(takeFromPast: boolean) {
    this.instructorService.getEnrolledUser(this.eventId!, takeFromPast).pipe(take(1)).subscribe(response => {
      this.enrolledUser = response.enrolledUserWithDebt;
    })
  }

  getUserDebt(enrolledUser: UserEnrolmentWithDebtTs): string {
    if (enrolledUser.debt === 0) return "Brak zaległości"
    else return "Zaległość do zapłaty: " + enrolledUser.debt.toString() + "zł"
  }

  deleteUserFromActivity(user: UserEnrolmentWithDebtTs) {
    let userName = this.fullnamePipe.transform(user.user)

    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: `Wypis z zajęć`,
        message: `Czy na pewno chcesz wypisać użytkownika ${userName} z zajęć?`
      },
    }).afterClosed().subscribe(isDialogConfirmed => {
      if (!isDialogConfirmed) {
        return;
      }

      this.instructorService.deleteUserFromActivities(this.eventId!, user.user.id).pipe(take(1)).subscribe({
        next: () => {
          this.enrolledUser = this.enrolledUser.filter(userEnrollment => userEnrollment != user)
          this.messageService.sendMessage("Użytkownik " + userName + " został wypisany z przyszłych zajęć.",
            NotificationMessageType.SUCCESS);
        },
        error: (error) => {
          this.messageService.sendMessage(error, NotificationMessageType.ERROR);
        }
      })
    })
  }

}

