import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {ActivatedRoute, Router} from "@angular/router";
import {map, Observable, startWith, take} from "rxjs";
import {MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {CategoryResponseTs} from "out/api/model/category-response";
import {encodeFilesToBase64} from "../../../core/util/encode-image";
import {ActivitiesService} from "../../activities/services/activities.service";
import {CreateActivityRequestTs, UserInfoTs, CreateSingleEventOccurenceTs} from "../../../../../out/api";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {FullnamePipe} from "../../../core/pipes/fullname.pipe";
import {formatDateAndTime} from "../../../core/util/date-utils";
import {Event} from "../../../core/components/calendar-form/calendar-form.component";
import {CreateSingleEventOccurenceTsFrequencyEnum} from 'out/api';

export interface Frequency {
  number: number;
  name: string;
  enumValue: CreateSingleEventOccurenceTsFrequencyEnum;
}

@Component({
  selector: 'app-new-activities-form',
  templateUrl: './new-activities-form.component.html',
  styleUrls: ['./new-activities-form.component.scss']
})
export class NewActivitiesFormComponent extends BaseComponent implements OnInit {
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
      minimalAge: new FormControl({ value: undefined, disabled: true }),
      maxAge: new FormControl({ value: undefined, disabled: true }),
      placeNumber: new FormControl({ value: undefined, disabled: true }),
      cost: new FormControl({ value: undefined, disabled: true }),
      images: new FormControl([]),
      eventsCalendar: new FormControl([])
    });
  }

  ngOnInit(): void {
    this.filteredHosts = this.activityForm.get("host")!.valueChanges.pipe(
      startWith(''),
      map(value => {
        const name = typeof value === 'string' ? value : value?.name;
        return name ? this._filter(name as string) : this.hosts.slice();
      }),
    );
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
    const singleEventsOccurrence = events.map(event => ({
      startTime: formatDateAndTime(event.startDate, event.startTime),
      endTime: formatDateAndTime(event.startDate, event.endTime),
      repeat: event.repeatNumber,
      frequency: event.frequency.enumValue,
    } as CreateSingleEventOccurenceTs));

    this.activityForm.get("eventsCalendar")?.setValue(singleEventsOccurrence);
  }

  private _filter(name: string): UserInfoTs[] {
    const filterValue = name.toLowerCase();
    return this.hosts.filter(option => this.fullNamePipe.transform(option).toLowerCase().includes(filterValue));
  }

  createNewActivity(): void {
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
    } as CreateActivityRequestTs

    this.activitiesService.createActivities(request).pipe(take(1)).subscribe({
      next: () => {
        this.router.navigateByUrl("/manage")
        this.messageService.sendMessage("Nowe zajęcia zostały dodane", NotificationMessageType.SUCCESS);
      },
      error: (error) => {
        switch (error.status) {
          case 422:
            this.messageService.sendMessage(
              "Wybrany instrukto prowadzi już zajęcia w którymś z określonych terminów.",
              NotificationMessageType.ERROR
            );
            break;
          case 404:
            this.messageService.sendMessage("Wybrano nieprawidłowy format zdjęć, kategorie lub instruktora. Spróbuj ponownie.", NotificationMessageType.ERROR);
            break;
          default:
            this.messageService.sendMessage(
              "Wystąpił nieoczekiwany błąd, spróbuj ponownie póżniej.",
              NotificationMessageType.ERROR
            );
            break;
        }
      }
    })
  }

  getInstructorIdByFullName(fullName: string){
    return this.hosts.find(host => this.fullNamePipe.transform(host) === fullName)?.id;
  }
}
