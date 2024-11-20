import {ResolveFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AccountService} from "../../my-account/service/account.service";
import {catchError, EMPTY} from "rxjs";
import {MessageService} from "../../../core/services/message.service";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {UserTs} from "../../../../../out/api/model/user";

export const loggedInUserResolver: ResolveFn<UserTs> = () => {
  const router = inject(Router)
  const messageService = inject(MessageService)

  return inject(AccountService).getUserInfo()
    .pipe(catchError(() => {
      messageService.sendMessage("Nie można pobrać danych użytkownika.", NotificationMessageType.ERROR)
      router.navigateByUrl("/my-activities")
      return EMPTY
    }));
};
