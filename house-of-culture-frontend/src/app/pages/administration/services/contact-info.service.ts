import { Injectable } from '@angular/core';
import {
  ContactInfoApiService,
  UpdateContactInfoRequestParams
} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class ContactInfoService{
  constructor(
    private service: ContactInfoApiService
  ) { }

  getContactInfo() {
    return this.service.getContactInfo()
  }

  updateContactInfo(contactInfo: UpdateContactInfoRequestParams) {
    return this.service.updateContactInfo(contactInfo)
  }
}
