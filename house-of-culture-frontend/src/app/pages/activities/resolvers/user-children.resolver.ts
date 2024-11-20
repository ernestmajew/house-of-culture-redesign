import {ResolveFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {MessageService} from "../../../core/services/message.service";
import {catchError, EMPTY} from "rxjs";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {UserService} from "../../my-account/service/user.service";
import {UserInfoTs} from "../../../../../out/api/model/user-info";

export const userChildrenResolver: ResolveFn<UserInfoTs[]> = () => {
  const router = inject(Router)
  const messageService = inject(MessageService)

  return inject(UserService).getChildren()
    .pipe(catchError(() => {
      messageService.sendMessage("Nie można pobrać danych dzieci użytkownika.", NotificationMessageType.ERROR)
      router.navigateByUrl("/my-activities")
      return EMPTY
    }));
};
