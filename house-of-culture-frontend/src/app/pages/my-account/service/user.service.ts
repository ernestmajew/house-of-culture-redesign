import { Injectable } from '@angular/core';
import {CreateUserRequestTs, UserApiService} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private service: UserApiService) { }

  createUser(request: CreateUserRequestTs){
    return this.service.createUser({createUserRequestTs: request});
  }

  getChildren(){
    return this.service.getChildren();
  }

}
