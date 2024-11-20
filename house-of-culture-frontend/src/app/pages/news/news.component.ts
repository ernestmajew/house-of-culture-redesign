import {Component} from '@angular/core';
import {BaseComponent} from "../../core/abstract-base/base.component";
import {NotificationMessageType} from "../../core/models/notification-message";

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: ['./news.component.scss']
})
export class NewsComponent extends BaseComponent {

  constructor() {
    super();
  }

  clickA() {
    this.messageService.sendMessage("a", NotificationMessageType.INFO)
  }
  clickB() {
    this.messageService.sendMessage("b", NotificationMessageType.WARNING)
  }
  clickC() {
    this.messageService.sendMessage("c", NotificationMessageType.SUCCESS)
  }
  clickD() {
    this.messageService.sendMessage("d", NotificationMessageType.ERROR)
  }
}
