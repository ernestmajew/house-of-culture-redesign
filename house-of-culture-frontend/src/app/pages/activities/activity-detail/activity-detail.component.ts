import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ActivityResponseTs} from "../../../../../out/api/model/activity-response";
import {ActivitiesService} from "../services/activities.service";
import {catchError, take} from "rxjs";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {ImageItem} from "ng-gallery";
import {UserRoleTs} from "../../../../../out/api/model/user-role";
import {SingleEventOccurenceTs} from "../../../../../out/api/model/single-event-occurence";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "../../../core/components/confirmation-dialog/confirmation-dialog.component";
import {DomSanitizer} from "@angular/platform-browser";

@Component({
  selector: 'app-activity-detail',
  templateUrl: './activity-detail.component.html',
  styleUrls: ['./activity-detail.component.scss']
})
export class ActivityDetailComponent extends BaseComponent implements OnInit {
  activity: ActivityResponseTs | undefined
  images: ImageItem[] = []

  ageLimitsText?: string

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private activitiesService: ActivitiesService,
    private dialog: MatDialog,
    private sanitizer: DomSanitizer
  ) {
    super()
  }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(params => {
      this.fetchActivityById(params['id'])
    });
  }

  fetchActivityById(id: number) {
    this.activitiesService.getActivityById(id)
      .pipe(take(1))
      .pipe(catchError(err => {
        if (err.status == 404) {
          this.messageService.sendMessage("Nie znaleziono zajęć.", NotificationMessageType.ERROR)
        } else {
          this.messageService.sendMessage(
            "Wystąpił błąd podczas pobierania informacji o zajęciach.",
            NotificationMessageType.ERROR
          )
        }
        this.navigateToActivitiesOffersPage()
        return err
      }))
      .subscribe(response => {
        this.activity = response
        this.setImages(this.activity!!)
        this.setAgeLimitsText(this.activity!!)
      })
  }

  sanitizeDescription() {
    if(this.activity)
      return this.sanitizer.bypassSecurityTrustHtml(this.activity!!.description)

    return ""
  }

  navigateToActivitiesOffersPage() {
    this.router.navigate(["../"], {relativeTo: this.activatedRoute});
  }

  private setAgeLimitsText(activity: ActivityResponseTs) {
    if(activity.minimumAge != undefined && activity.maximumAge != undefined) {
      this.ageLimitsText = `${activity.minimumAge} - ${activity.maximumAge}`
    } else if(activity.minimumAge != undefined) {
      this.ageLimitsText = `${activity.minimumAge} <`
    } else if(activity.maximumAge != undefined) {
      this.ageLimitsText = `< ${activity.maximumAge}`
    }
  }

  private setImages(activity: ActivityResponseTs) {
    if(activity.images.length > 0) {
      this.images = activity.images.map((url, index) => {
        return new ImageItem({
          src: `${this.imagesBasePath}${url}`,
          thumb: `${this.imagesBasePath}${url}`
        })
      })
    } else {
      this.images = [new ImageItem({src: "/assets/noImage.jpg", thumb: "/assets/noImage.jpg"})]
    }
  }

  hasOccurrencePassed(singleEvent: SingleEventOccurenceTs) {
    const eventTime = new Date(singleEvent.startTime)
    const currentTime = new Date();
    return eventTime < currentTime;
  }

  deleteActivity() {
    this.dialog.open(ConfirmationDialogComponent, {
      data: {
        title: "Usuwanie zajęć",
        message: `Czy na pewno chcesz usunąć zajęcia "${this.activity?.title}"?`
      },
    }).afterClosed().subscribe(isDialogConfirmed => {
      if (!isDialogConfirmed) {
        return;
      }

      this.activitiesService.deleteActivityById(this.activity?.id!!).pipe(catchError(err => {
        if (err.status == 404) {
          this.messageService.sendMessage("Nie znaleziono zajęć.", NotificationMessageType.ERROR)
        } else {
          this.messageService.sendMessage(
            "Wystąpił błąd podczas usuwania zajęć.",
            NotificationMessageType.ERROR
          )
        }
        return err
      })).subscribe(_ => this.navigateToActivitiesOffersPage())
    })
  }

  editActivity() {
    this.router.navigateByUrl(`/manage/admin/edit-activity/${this.activity?.id}`)
  }

  enroll() {
    this.router.navigateByUrl(`/activities/enroll/${this.activity?.id}`)
  }

  enrolledUserView() {
    this.router.navigateByUrl(`/activities/enrollment/${this.activity?.id}`)
  }

  protected readonly UserRoleTs = UserRoleTs;
}
