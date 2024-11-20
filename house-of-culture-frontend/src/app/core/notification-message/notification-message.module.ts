import {NgModule} from "@angular/core";
import {NotificationMessageComponent} from "./notification-message.component";
import {AlertTypeIconPipe} from "./pipes/alert-type-icon.pipe";
import {CommonModule} from "@angular/common";
import {AngularMaterialModule} from "../modules/angular-material.module";

@NgModule({
  declarations: [
    AlertTypeIconPipe,
    NotificationMessageComponent
  ],
  imports: [
    AngularMaterialModule,
    CommonModule
  ],
  exports: [NotificationMessageComponent]
})
export class NotificationMessageModule {
}
