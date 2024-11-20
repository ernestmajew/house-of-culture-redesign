import {Injectable} from '@angular/core';
import {UserApiService} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class InstructorsService {

  constructor(private service: UserApiService) {
  }

  getInstructors(){
    return this.service.getAllInstructors();
  }
}
