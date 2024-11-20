import {Observable, Subject} from "rxjs";
import {NotificationMessage, NotificationMessageType} from "../models/notification-message";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  public subject = new Subject<NotificationMessage>();

  sendMessage(message: string, type: NotificationMessageType): void {
    this.subject.next({message, type});
  }

  clearMessage(): void {
    this.subject.next({message: "", type: NotificationMessageType.CLEAR});
  }

  getMessage(): Observable<NotificationMessage> {
    return this.subject.asObservable();
  }
}
