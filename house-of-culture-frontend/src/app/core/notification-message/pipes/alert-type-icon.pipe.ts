import {Pipe, PipeTransform} from "@angular/core";
import {NotificationMessage, NotificationMessageType} from "../../models/notification-message";

@Pipe({
  name: 'alertTypeIcon'
})
export class AlertTypeIconPipe implements PipeTransform {

  transform(alert: NotificationMessage): string {
    switch (alert.type){
      case NotificationMessageType.SUCCESS:
        return "check_circle";
      case NotificationMessageType.INFO:
        return "info";
      case NotificationMessageType.WARNING:
        return "warning";
      case NotificationMessageType.ERROR:
        return "error";
      default:
        return "";
    }
  }

}
