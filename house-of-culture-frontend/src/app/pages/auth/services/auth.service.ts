import { Injectable } from '@angular/core';
import {AuthApiService, AuthRequestTs, RegisterRequestTs} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private service :AuthApiService) { }

  login(authRequestTs: AuthRequestTs) {
    return this.service.authenticate({authRequestTs: authRequestTs});
  }

  register(registerRequestTs: RegisterRequestTs) {
    return this.service.register({registerRequestTs: registerRequestTs});
  }

}
