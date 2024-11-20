import {NgModule} from "@angular/core";
import {MyActivitiesComponent} from './my-activities/my-activities.component';
import {ActivitiesOffersComponent} from './activities-offers/activities-offers.component';
import {ActivitiesComponent} from './activities.component';
import {AppRoutingModule} from "../../core/modules/app-routing.module";
import {AngularMaterialModule} from "../../core/modules/angular-material.module";
import {ActivityDatePipe} from './pipes/activity-date.pipe';
import {ActivityAgePipe} from './pipes/activity-age.pipe';
import {ActivityOfferCardComponent} from "./activity-offer-card/activity-offer-card.component";
import {CommonModule, NgClass, NgForOf, NgIf} from "@angular/common";
import {ActivitiesRowComponent} from "./activities-row/activities-row.component";
import {SharedComponentsModule} from "../../core/components/shared-components.module";
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatProgressBarModule} from "@angular/material/progress-bar";
import {ActivityDetailComponent} from "./activity-detail/activity-detail.component";
import {GalleryComponent} from "ng-gallery";
import {GalleryModule} from "@ks89/angular-modal-gallery";
import {GallerizeDirective} from "ng-gallery/lightbox";
import {CalendarComponent} from "../../core/components/calendar/calendar.component";
import {ActivityCostPipe} from './activity-detail/pipes/activity-cost.pipe';
import {ActivityAvailablePlacesPipe} from './activity-detail/pipes/activity-available-places.pipe';
import {AccessDirective} from "../../core/guards/directive/access.directive";
import {MatExpansionModule} from "@angular/material/expansion";
import {ActivityOccurrencePipe} from './activity-detail/pipes/activity-occurrence.pipe';
import {NgxEditorModule} from "ngx-editor";
import {PaginatorModule} from "primeng/paginator";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";
import {EditActivitiesComponent} from './edit-activities/edit-activities.component';
import {FullnamePipe} from "../../core/pipes/fullname.pipe";
import {ActivitiesCalendarComponent} from "../../core/components/calendar/activities-calendar/activities-calendar.component";
import {MatTabsModule} from "@angular/material/tabs";
import {ActivityEnrollComponent} from './activity-enroll/activity-enroll.component';
import {MatRadioModule} from "@angular/material/radio";
import {MatInputModule} from "@angular/material/input";
import {EnrollOccurrencesSelectorComponent} from './activity-enroll/enroll-occurrences-selector/enroll-occurrences-selector.component';
import {MyActivitiesListComponent} from "./my-activities/my-activities-list/my-activities-list.component";
import {SignOutEventsPopupComponent} from './my-activities/sign-out-events-popup/sign-out-events-popup.component';
import { ActivityPaymentsComponent } from './activity-payments/activity-payments.component';
import {PayuButtonComponent} from "../../core/components/payu-button/payu-button.component";
import {ActivityCardComponent} from "./my-activities/my-activities-list/activity-card/activity-card.component";
import { PaymentStatusPipe } from './pipes/payment-status.pipe';
import {PaymentStatusStyleDirective} from "./directives/payment-status-style.directive";
import { ActivityPeriodicPaymentComponent } from './activity-periodic-payment/activity-periodic-payment.component';
import {MultiSelectModule} from "primeng/multiselect";

@NgModule({
  declarations: [
    MyActivitiesComponent,
    ActivitiesOffersComponent,
    ActivitiesComponent,
    ActivityDatePipe,
    ActivityAgePipe,
    ActivityOfferCardComponent,
    ActivitiesRowComponent,
    ActivityDetailComponent,
    ActivityCostPipe,
    ActivityAvailablePlacesPipe,
    ActivityOccurrencePipe,
    EditActivitiesComponent,
    ActivityEnrollComponent,
    EnrollOccurrencesSelectorComponent,
    SignOutEventsPopupComponent,
    ActivityPaymentsComponent,
    PaymentStatusPipe,
    PaymentStatusStyleDirective,
    ActivityPeriodicPaymentComponent
  ],
    imports: [
        CommonModule,
        AngularMaterialModule,
        AppRoutingModule,
        CalendarComponent,
        NgxEditorModule,
        PaginatorModule,
        ReactiveFormsModule,
        SharedComponentsModule,
        NgxMaterialTimepickerModule.setOpts('pl-PL'),
        GalleryModule,
        GalleryComponent,
        GallerizeDirective,
        NgForOf,
        NgIf,
        FormsModule,
        MatProgressBarModule,
        AccessDirective,
        MatExpansionModule,
        NgClass,
        FullnamePipe,
        MatTabsModule,
        MatRadioModule,
        MatInputModule,
        MyActivitiesListComponent,
        ActivitiesCalendarComponent,
        PayuButtonComponent,
        ActivityCardComponent,
        MultiSelectModule
    ],
  exports: [
    ActivitiesComponent,
    ActivityDatePipe,
    ActivityAgePipe
  ],
  providers: []
})
export class ActivitiesModule {
}
