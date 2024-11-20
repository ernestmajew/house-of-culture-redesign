import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {ContactInfoTs} from "../../../../../out/api";
import {ContactInfoService} from "../services/contact-info.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-contact-info-form',
  templateUrl: './contact-info-form.component.html',
  styleUrls: ['./contact-info-form.component.scss']
})
export class ContactInfoFormComponent extends BaseComponent implements OnInit {
  constructor(private formBuilder: FormBuilder,
              private service: ContactInfoService,
              private router: Router,
              private activatedRoute: ActivatedRoute,
              ){
    super();
  }

  contactInfoForm!: FormGroup;

  ngOnInit() {
    this.contactInfoForm = this.formBuilder.group({
      addressFirstLine: ['', [Validators.required, Validators.minLength(1)]],
      addressSecondLine: ['', [Validators.required, Validators.minLength(1)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern('(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)')]],
      facebookUrl: [undefined, [Validators.pattern('^https://facebook.com/.*$')]],
      instagramUrl: [undefined, [Validators.pattern('^https://instagram.com/.*$')]],
      latitude: ['', [Validators.pattern(/^-?[0-9]+(?:\.[0-9]+)?$/)]],
      longitude: ['', [Validators.pattern(/^-?[0-9]+(?:.[0-9]+)?$/)]]
    })

    this.contactInfoForm.get('phone_number')?.valueChanges.subscribe(newValue => {
      this.onPhoneNumberChange(newValue)
    })

    this.fetchContactInfo()
  }

  fetchContactInfo() {
    this.service.getContactInfo().subscribe((contactInfo: ContactInfoTs) => {
      this.contactInfoForm.patchValue(contactInfo)
    })
  }

  onSubmit() {
    let contactInfo: ContactInfoTs = {institutionName: '', ...this.contactInfoForm.value}
    this.service
      .updateContactInfo({contactInfoTs: contactInfo})
      .subscribe({
        next: (contactInfo: ContactInfoTs) => {
          this.contactInfoForm.patchValue(contactInfo)
          this.messageService.sendMessage(
            "Dane kontaktowe zostały zaktualizowane.",
            NotificationMessageType.SUCCESS
          )
          this.backToPreviousPage();
        },
        error: (error: any) => {
          this.messageService.sendMessage(
            "Dane są niepoprawne.",
            NotificationMessageType.ERROR
          )
        }
      })
  }

  private onPhoneNumberChange(newValue: string) {
    let formattedValue = this.formatPhoneNumber(newValue)

    if (formattedValue !== newValue) {
      this.contactInfoForm.patchValue(
        {phone_number: formattedValue}
      )
    }
  }

  // TODO: move to some utils dir or sth
  private formatPhoneNumber(phoneNumber: string) {
    const removedWhitespaces = phoneNumber.replace(/\s+/g, '')

    // insert whitespace after every 3 chars
    return removedWhitespaces.replace(/(.{3})/g, '$1 ').trim()
  }

  backToPreviousPage() {
    this.router.navigate(["../../"], {relativeTo: this.activatedRoute});
  }
}
