import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {map, Observable, startWith, switchMap, take} from "rxjs";
import {MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {ActivatedRoute, Router} from "@angular/router";
import {ActivitiesService} from "../services/activities.service";
import {FullnamePipe} from "../../../core/pipes/fullname.pipe";
import {encodeFilesToBase64} from "../../../core/util/encode-image";
import {Event} from "../../../core/components/calendar-form/calendar-form.component";
import {formatDateAndTime, parseUTCDateWithoutTimezoneOffset} from "../../../core/util/date-utils";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {
  ActivityResponseTs,
  EditActivityRequestTs,
  SingleEventOccurenceTs,
  SingleEventRequestTs,
  UserInfoTs
} from "../../../../../out/api";
import {CategoryResponseTs} from "out/api/model/category-response";
import {Frequency} from "../../administration/create-new-activities/new-activities-form.component";
import {CreateSingleEventOccurenceTsFrequencyEnum} from 'out/api';

@Component({
  selector: 'app-edit-activities',
  templateUrl: './edit-activities.component.html',
  styleUrls: ['./edit-activities.component.scss']
})
export class EditActivitiesComponent extends BaseComponent implements OnInit {
  activityToEdit!: ActivityResponseTs;
  id?: number;
  imagesFiles?: string[];
  events?: Event[];

  activityForm: FormGroup;

  hosts: UserInfoTs[] = []

  placeholder: string = "Wpisz opis zajęć tutaj..."
  errorMessage: string = "Opis jest wymagany."

  frequencies: Frequency[] = [
    {number: 1, name: "Jednorazowo", enumValue: CreateSingleEventOccurenceTsFrequencyEnum.SINGLE},
    {number: 7, name: "Co tydzień", enumValue: CreateSingleEventOccurenceTsFrequencyEnum.EVERY_WEEK},
    {number: 14, name: "Co dwa tygodnie", enumValue: CreateSingleEventOccurenceTsFrequencyEnum.EVERY_TWO_WEEKS},
    {number: 21, name: "Co trzy tygodnie", enumValue: CreateSingleEventOccurenceTsFrequencyEnum.EVERY_THREE_WEEKS}
  ];

  filteredHosts?: Observable<UserInfoTs[]>;
  @ViewChild(MatAutocompleteTrigger) autocomplete?: MatAutocompleteTrigger;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private activitiesService: ActivitiesService,
              private fullNamePipe: FullnamePipe) {
    super();

    this.route.data.subscribe(
      ({instructors}) => {
        this.hosts = instructors
      }
    );

    this.activityForm = new FormGroup({
      name: new FormControl("", Validators.required),
      editorContent: new FormControl("", Validators.required),
      categories: new FormControl([]),
      host: new FormControl<string | UserInfoTs>("", Validators.required),
      minimalAge: new FormControl({value: undefined, disabled: true}),
      maxAge: new FormControl({value: undefined, disabled: true}),
      placeNumber: new FormControl({value: undefined, disabled: true}),
      cost: new FormControl({value: undefined, disabled: true}),
      images: new FormControl([]),
      eventsCalendar: new FormControl([])
    });
  }

  ngOnInit(): void {
    this.route.params.pipe(
      switchMap((params) => {
        this.id = +params['id'];
        return this.activitiesService.getActivityById(this.id);
      }),
      take(1)
    ).subscribe(response => {
      this.activityToEdit = response;
      this.patchForm();
    });

    this.filteredHosts = this.activityForm.get("host")!.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.name;
        return name ? this._filter(name as string) : this.hosts.slice();
      }),
    );
  }

  patchForm() {
    if (this.activityToEdit) {
      this.activityForm.patchValue({
        name: this.activityToEdit.title,
        editorContent: this.activityToEdit.description,
        categories: this.activityToEdit.categories,
        host: this.activityToEdit.instructor_name,
        minimalAge: this.activityToEdit.minimumAge,
        maxAge: this.activityToEdit.maximumAge,
        placeNumber: this.activityToEdit.maxPlaces,
        cost: this.activityToEdit.cost,
        images: this.activityToEdit.images,
        eventsCalendar: this.activityToEdit.occurences,
      });
    }

    this.imagesFiles = this.activityToEdit.images.map(image => {
      return this.imagesBasePath + image
    })

    this.getCalendarEventsFromSingleEventOccurrences(this.activityToEdit.occurences);
  }

  toggleFormControl(controlName: string) {
    const control = this.activityForm.get(controlName);
    control!.disabled ? control!.enable() : control!.disable();
  }

  onHtmlContentChange(htmlContent: string): void {
    this.activityForm.get("editorContent")?.setValue(htmlContent);
  }

  onChosenCategoriesChange(value: CategoryResponseTs[]): void {
    this.activityForm.get("categories")?.setValue(value);
  }

  onUploadImageChange(files: File[]): void {
    encodeFilesToBase64(files).then((base64Array) => {
      this.activityForm.get("images")?.setValue(base64Array)
    });
  }

  onSelectedEventChange(events: Event[]): void {
    const singleEventsOccurrence = events.flatMap(event => (
        this.createSingleEvents(event)
    ));

    this.activityForm.get("eventsCalendar")?.setValue(singleEventsOccurrence);
  }

  private createSingleEvents(event: Event) {
    let singleEvents: SingleEventRequestTs[] = []

    const start = event.startDate;
    const [startHour, startMinute] = event.startTime.split(':').map(Number);
    const [endHour, endMinute] = event.endTime.split(':').map(Number);

    for (let i = 0; i < event.repeatNumber; i++) {
      const eventDate = new Date(start.getTime() + i * event.frequency.number * 24 * 60 * 60 * 1000);
      eventDate.setHours(startHour, startMinute);

      const eventEndDate = new Date(eventDate);
      eventEndDate.setHours(endHour, endMinute);

      const singleEvent: SingleEventRequestTs = {
        startTime: eventDate.toISOString(),
        endTime: eventEndDate.toISOString(),
      };

      singleEvents.push(singleEvent);
    }

    return singleEvents
  }

  private _filter(name: string): UserInfoTs[] {
    const filterValue = name.toLowerCase();
    return this.hosts.filter(option => this.fullNamePipe.transform(option).toLowerCase().includes(filterValue));
  }

  editActivity(): void {
    let request = {
      title: this.activityForm.get("name")?.value,
      description: this.activityForm.get("editorContent")?.value,
      categories: this.activityForm.get("categories")?.value,
      minimumAge: this.activityForm.get("minimalAge")?.value,
      maximumAge: this.activityForm.get("maxAge")?.value,
      maxPlaces: this.activityForm.get("placeNumber")?.value,
      cost: this.activityForm.get("cost")?.value,
      images: this.activityForm.get("images")?.value,
      instructor_id: this.getInstructorIdByFullName(this.activityForm.get("host")?.value),
      occurences: this.activityForm.get("eventsCalendar")?.value
    } as EditActivityRequestTs

    this.activitiesService.editActivities(this.id!, request)
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.router.navigateByUrl("/activities/" + this.id!)
          this.messageService.sendMessage("Zajęcia zostały pomyślnie wyedytowane", NotificationMessageType.SUCCESS);
        },
        error: (error) => {
          switch (error.status){
            case 422:
              this.messageService.sendMessage("Instruktor posiada już zajęcia w danym terminie.", NotificationMessageType.ERROR);
              break;
            case 404:
              this.messageService.sendMessage("Nie ma takich zajęć, kategorii lub instruktora w systemie. Spróbuj ponownie.", NotificationMessageType.ERROR);
              break;
            default:
              this.messageService.sendMessage("Wystąpił nieoczekiwany błąd. Spawdź poprawność pól i spróbuj ponownie.", NotificationMessageType.ERROR);
          }
        }
      })
  }

  private getInstructorIdByFullName(fullName: string) {
    return this.hosts.find(host => this.fullNamePipe.transform(host) === fullName)?.id;
  }

  private getCalendarEventsFromSingleEventOccurrences(singleEventOccurrences: SingleEventOccurenceTs[]) {
    this.events = singleEventOccurrences.map(event => {
      let startDate = new Date(parseUTCDateWithoutTimezoneOffset(event.startTime));
      let endDate = new Date(parseUTCDateWithoutTimezoneOffset(event.endTime));
      return {
        id: event.id,
        startDate: startDate,
        repeatNumber: 1,
        frequency: {number: 1, name: '', enumValue: CreateSingleEventOccurenceTsFrequencyEnum.SINGLE} as Frequency,
        startTime: this.getTime(startDate),
        endTime: this.getTime(endDate),
        isCanceled: event.isCancelled
      } as Event
    })
  }

  private getTime(date: Date) {
    return date.getHours().toString() + ":" + date.getMinutes().toString() + "0"
  }
}
