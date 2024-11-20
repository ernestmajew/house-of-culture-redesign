import {CanActivateChildFn, Router} from '@angular/router';
import {JwtTokenService} from "../../pages/auth/services/jwt-token.service";
import {inject} from "@angular/core";

export const loggedUserGuard: CanActivateChildFn = () => {
  if(inject(JwtTokenService).jwtToken()){
    return true
  }
  inject(Router).navigate(['/login']);
  return false;
};
