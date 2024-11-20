import { Injectable } from '@angular/core';
import {AuthApiService, ChangePasswordRequestRequestTs, PasswordChangeRequestTs} from "../../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class PasswordChangeService {

  constructor(private service: AuthApiService) { }


  changePasswordRequest(changePasswordRequestTs: ChangePasswordRequestRequestTs){
    return this.service.changePasswordRequest({changePasswordRequestRequestTs: changePasswordRequestTs});
  }

  validateChangePasswordCode(uuid: string, code: string){
    return this.service.validateChangePasswordCode({uuid: uuid, code: code});
  }

  changeUserPassword(passwordChangeRequestTs: PasswordChangeRequestTs){
    return this.service.changeUserPassword({passwordChangeRequestTs: passwordChangeRequestTs});
  }

  getPasswordChangeInfo(uuid: string){
    return this.service.getPasswordChangeInfo({uuid: uuid});
  }
}
