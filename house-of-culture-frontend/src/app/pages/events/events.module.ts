import {NgModule} from "@angular/core";
import {EventsComponent} from './events.component';
import {AppRoutingModule} from "../../core/modules/app-routing.module";
import {AngularMaterialModule} from "../../core/modules/angular-material.module";
import {PayuButtonComponent} from "../../core/components/payu-button/payu-button.component";

@NgModule({
  declarations: [
    EventsComponent
  ],
    imports: [
        AngularMaterialModule,
        AppRoutingModule,
        PayuButtonComponent
    ],
  exports: [
    EventsComponent
  ],
  providers: []
})
export class EventsModule {
}
