import {Component, inject, OnDestroy} from '@angular/core';
import {Observable, Subject} from "rxjs";
import {NotificationMessage, NotificationMessageType} from "../models/notification-message";
import {MessageService} from "../services/message.service";

@Component(
  {template: ''}
)
export class BaseComponent implements OnDestroy {

  public messageService: MessageService = inject(MessageService);
  public imagesBasePath: string = "http://localhost:8080/api/public/image";
  protected destroyed$ = new Subject<boolean>()

  constructor() {
  }

  sendMessage(message: string, type: NotificationMessageType): void {
    this.messageService.sendMessage(message, type);
  }

  clearMessage(): void {
    this.messageService.clearMessage();
  }

  getMessage(): Observable<NotificationMessage> {
    return this.messageService.getMessage();
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

}
