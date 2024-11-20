import {Component} from '@angular/core';
import {BaseComponent} from "../abstract-base/base.component";
import {takeUntil} from "rxjs";
import {NotificationMessage, NotificationMessageType} from "../models/notification-message";

@Component({
  selector: 'app-notification-message',
  templateUrl: './notification-message.component.html',
  styleUrls: ['./notification-message.component.scss']
})
export class NotificationMessageComponent extends BaseComponent {

  public alerts: NotificationMessage[] = [];
  public NotificationMessageType = NotificationMessageType;
  private TIMEOUT = 10000;
  private MAX_NUMBER_OF_NOTIFICATION = 3;

  constructor() {
    super();
    this.messageService.getMessage().pipe(takeUntil(this.destroyed$)).subscribe(alert => {
      if (this.alerts.length < this.MAX_NUMBER_OF_NOTIFICATION){
        this.alerts.reverse();
        this.alerts.push(alert);
        this.alerts.reverse();
      }
      else {
        this.alerts.pop();
        this.alerts.reverse();
        this.alerts.push(alert);
        this.alerts.reverse();
      }
      setTimeout(() => this.removeAlert(alert), this.TIMEOUT);
    });
  }

  removeAlert(alert: NotificationMessage): void {
    this.alerts = this.alerts.filter(listAlertInstance => listAlertInstance !== alert);
  }
}
