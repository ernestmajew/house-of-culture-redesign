import { Injectable } from '@angular/core';
import { UpdateUserRequestTs, UserApiService} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  constructor(private service: UserApiService) { }

  getUserInfo(){
    return this.service.getAccount();
  }

  updateUserInfo(updateUserRequest: UpdateUserRequestTs){
    return this.service.updateAccount({updateUserRequestTs: updateUserRequest});
  }
}
