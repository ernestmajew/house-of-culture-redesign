import {CanActivateFn, Router} from '@angular/router';
import {JwtTokenService} from "../../pages/auth/services/jwt-token.service";
import {inject} from "@angular/core";

export const unloggedUserGuard: CanActivateFn = () => {
  if (!inject(JwtTokenService).jwtToken()) {
    return true
  }
  inject(Router).navigate(['/']);
  return false;
};
