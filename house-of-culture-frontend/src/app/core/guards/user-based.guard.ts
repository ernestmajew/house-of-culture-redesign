import {CanActivateChildFn, Router} from '@angular/router';
import {JwtTokenService} from "../../pages/auth/services/jwt-token.service";
import {inject} from "@angular/core";
import {UserRoleTs} from "../../../../out/api";
import {MessageService} from "../services/message.service";
import {NotificationMessageType} from "../models/notification-message";

export function userRoleGuard(role: UserRoleTs): CanActivateChildFn {
  return () => {
    const decodedToken = inject(JwtTokenService).decodedToken;

    if (decodedToken && decodedToken.roles.includes(role.toString())) {
      return true
    }
    inject(Router).navigate(['/']);
    inject(MessageService).sendMessage("Nie masz uprawnień do tej strony", NotificationMessageType.ERROR)
    return false;
  }
}
