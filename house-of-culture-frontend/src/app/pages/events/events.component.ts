import {Component} from '@angular/core';
import {BaseComponent} from "../../core/abstract-base/base.component";
import {HttpClient} from "@angular/common/http";
import {NotificationMessageType} from "../../core/models/notification-message";
import {catchError} from "rxjs";

@Component({
  selector: 'app-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent extends BaseComponent {
  constructor(
    private http: HttpClient
  ) {
    super();
  }

  //ony for test purposes

  payuOrderRequest() {
    this.http.post('/api/payu', null)
      .pipe(
        catchError((error) => {
          this.messageService.sendMessage("Transaction failed", NotificationMessageType.ERROR);
          throw error;
        })
      )
      .subscribe((data) => {
        if (data && "redirectUri" in data) {
            window.open(data.redirectUri as string, "_self");
        }
      });
  }
}
