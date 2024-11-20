import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {ActivatedRoute, Router} from "@angular/router";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {EnrollmentService} from "../services/enrollment.service";
import {UserEnrollmentAvailabilityResponseTs} from "../../../../../out/api/model/user-enrollment-availability-response";
import {EnrollmentAvailabilityResponseTs} from "../../../../../out/api/model/enrollment-availability-response";
import {SingleEventOccurenceTs} from "../../../../../out/api/model/single-event-occurence";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../../../core/components/confirmation-dialog/confirmation-dialog.component";
import {EnrollmentAvailabilityStatusTs} from "../../../../../out/api/model/enrollment-availability-status";

interface ActivityBasicInfo {
  id: number;
  title?: string;
  cost?: number
}

@Component({
  selector: 'app-activity-enroll',
  templateUrl: './activity-enroll.component.html',
  styleUrls: ['./activity-enroll.component.scss']
})
export class ActivityEnrollComponent extends BaseComponent implements OnInit{
  activityBasicInfo?: ActivityBasicInfo
  usersAvailableSingleEvents: UserEnrollmentAvailabilityResponseTs[] = []

  enrollmentForm!: FormGroup

  closestAvailableSingleEventText: string = ""
  selectedOccurrencesNumber: number = 1
  maxOccurrences: number = 1

  // summary
  totalCost: string = ""
  selectedOccurrencesTimeRange: string = ""

  constructor(
    private formBuilder: FormBuilder,
    private enrollmentService: EnrollmentService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private dialog: MatDialog
  ) {
    super();
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      const activityId = params['id']
      this.activityBasicInfo = {
        ...this.activityBasicInfo,
        id: activityId,
      }
      this.fetchEnrollmentAvailability(activityId)
    });

    this.enrollmentForm = this.formBuilder.group({
      userId: [undefined, [Validators.required]],
      occurrencesNumber: [1, [Validators.required, Validators.min(1)]],
    })

    this.enrollmentForm.controls['occurrencesNumber'].valueChanges.subscribe((value: number) => {
      this.selectedOccurrencesNumber = value
      this.buildSummary()
    })

    this.enrollmentForm.controls['userId'].valueChanges.subscribe(() => {
      this.updateEnrollmentForm() // set new validations based on user
      this.buildSummary()
      this.updateClosestAvailableSingleEventText()
    })
  }

  fetchEnrollmentAvailability(activityId: number) {
    this.enrollmentService.getEnrollmentAvailability(activityId).subscribe({
      next: (response: EnrollmentAvailabilityResponseTs) => {
        this.activityBasicInfo = {
          id: activityId,
          title: response.title,
          cost: response.cost
        }
        this.usersAvailableSingleEvents = response.usersAvailability
        this.selectFirstAvailableUser()
      },
      error: (err) => {
        if (err.status == 404) {
          this.sendMessage("Nie znaleziono podanych zajęć.", NotificationMessageType.ERROR)
          this.navigateToActivitiesOffers()
        } else if (!err.ok) {
          this.sendMessage("Nie udało się pobrać danych potrzebnych do zapisu.", NotificationMessageType.ERROR)
          this.navigateToActivityDetails()
        }

        return err
      }
    })
  }

  getSelectedUserAvailableSingleEvents(): SingleEventOccurenceTs[] {
    const selectedUserId = this.enrollmentForm.controls['userId']?.value
    return this.usersAvailableSingleEvents
      .filter(elem => elem.userId === selectedUserId)
      .at(0)?.singleEvents ?? []
  }

  updateEnrollmentForm() {
    let occurrencesControl = this.enrollmentForm.controls['occurrencesNumber']
    let selectedUserMaxOccurrences = this.getSelectedUserAvailableSingleEvents().length

    occurrencesControl.clearValidators()
    occurrencesControl.addValidators([
      Validators.required,
      Validators.min(1),
      Validators.max(selectedUserMaxOccurrences)
    ])
    occurrencesControl.updateValueAndValidity()

    this.maxOccurrences = selectedUserMaxOccurrences
  }

  showEnrollDialog() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: `Czy na chcesz zapisać ${this.getSelectedUserFullName()} na zajęcia?`,
        message: ""
      },
    }).afterClosed().subscribe(isDialogConfirmed => {
      if(isDialogConfirmed) this.enroll()
    })
  }

  enroll() {
    this.enrollmentService.enrollToEvent(
      this.activityBasicInfo!!.id,
      this.enrollmentForm.controls['occurrencesNumber'].value,
      this.enrollmentForm.controls['userId'].value,
    ).subscribe({
      next: (_) => {
        this.navigateToEnrollmentPayment()
      },
      error: err => {
        this.sendMessage("Nie udało się zapisać użytkownika na zajęcia.", NotificationMessageType.ERROR)
        this.navigateToActivityDetails()
        return err
      }
    })
  }

  setMaxOccurrences() {
    const maxForSelectedUser = this.getSelectedUserAvailableSingleEvents().length
    this.enrollmentForm.controls['occurrencesNumber'].setValue(maxForSelectedUser)
  }

  getUserTooltip(status: EnrollmentAvailabilityStatusTs) {
    if(status == EnrollmentAvailabilityStatusTs.ALREADY_ENROLLED)
      return "Użytkownik jest już w pełni zapisany na wybrane zajęcia."

    if(status == EnrollmentAvailabilityStatusTs.TOO_YOUNG)
      return "Użytkownik jest za młody na wybrane zajęcia."

    if(status == EnrollmentAvailabilityStatusTs.TOO_OLD)
      return "Użytkownik jest za stary na wybrane zajęcia."

    return ""
  }

  private selectFirstAvailableUser() {
    const firstAvailableUser = this.usersAvailableSingleEvents.find(elem =>
      elem.status == EnrollmentAvailabilityStatusTs.AVAILABLE
    )?.userId
    this.enrollmentForm.controls['userId'].setValue(firstAvailableUser)
  }

  private getSelectedUserFullName() {
    const selectedUserId = this.enrollmentForm.controls['userId'].value
    return this.usersAvailableSingleEvents
      .filter(elem => elem.userId === selectedUserId)
      .at(0)?.userFullName
  }

  private buildSummary() {
    const selectedOccurrencesNumber = this.selectedOccurrencesNumber

    this.totalCost = this.activityBasicInfo?.cost
      ? `${(selectedOccurrencesNumber * this.activityBasicInfo!!.cost).toFixed(2)} zł`
      : "Darmowe";

    let selectedOccurrences =
      this.getSelectedUserAvailableSingleEvents().slice(0, selectedOccurrencesNumber)

    let firstSelectedOccurrence = selectedOccurrences[0]
    let lastSelectedOccurrence = selectedOccurrences[selectedOccurrences.length-1]

    this.selectedOccurrencesTimeRange = `
      od ${(new Date(firstSelectedOccurrence.startTime)).toLocaleDateString()}
      do ${(new Date(lastSelectedOccurrence.startTime)).toLocaleDateString()}
      `.trim()
  }

  private updateClosestAvailableSingleEventText() {
    const selectedUserFirstEvent = this.getSelectedUserAvailableSingleEvents().at(0)
    if (selectedUserFirstEvent) {
      this.closestAvailableSingleEventText = new Date(selectedUserFirstEvent!!.startTime).toLocaleDateString()
    }
  }

  public navigateToActivityDetails() {
    this.router.navigateByUrl(`/activities/${this.activityBasicInfo?.id}`)
  }

  private navigateToEnrollmentPayment() {
    const selectedUserId = this.enrollmentForm.controls['userId'].value
    this.router.navigateByUrl(`/my-activities/${this.activityBasicInfo?.id}/payment/${selectedUserId}`)
  }

  private navigateToActivitiesOffers() {
    this.router.navigateByUrl(`/activities`)
  }

  protected readonly FormControl = FormControl;
  protected readonly EnrollmentAvailabilityStatusTs = EnrollmentAvailabilityStatusTs;
}
