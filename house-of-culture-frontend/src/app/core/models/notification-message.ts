export interface NotificationMessage {
  type: NotificationMessageType;
  message: string;
}

export enum NotificationMessageType {
  SUCCESS = "SUCCESS",
  ERROR = "ERROR",
  INFO = "INFO",
  WARNING = "WARNING",
  CLEAR = ""
}
