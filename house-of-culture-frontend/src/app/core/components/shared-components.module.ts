import {NgModule} from "@angular/core";
import {ImagesUploadComponent} from "./images-upload/images-upload.component";
import {CdkDrag, CdkDropList} from "@angular/cdk/drag-drop";
import {CommonModule, NgOptimizedImage} from "@angular/common";
import {PaginationComponent} from "./pagination/pagination/pagination.component";
import {GalleryModule} from "@ks89/angular-modal-gallery";
import {AngularMaterialModule} from "../modules/angular-material.module";
import {EditorComponent} from "./editor/editor.component";
import {NgxEditorModule} from "ngx-editor";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CreateCategoryComponent} from "./category-selector/create-category/create-category.component";
import {MultiSelectModule} from "primeng/multiselect";
import {CategorySelectorComponent} from "./category-selector/category-selector.component";
import {AccessDirective} from "../guards/directive/access.directive";
import {CategoryFilterComponent} from "./category-filter/category-filter.component";
import {MatBadgeModule} from "@angular/material/badge";
import {AddedEventCardsComponent} from "./calendar-form/added-event-card/added-event-cards.component";
import {CalendarFormComponent} from "./calendar-form/calendar-form.component";
import {CalendarComponent} from "./calendar/calendar.component";
import {NgxMaterialTimepickerModule} from "ngx-material-timepicker";
import {ConfirmationDialogComponent} from './confirmation-dialog/confirmation-dialog.component';
import {EditSingleEventPopupComponent} from "./edit-single-event-popup/edit-single-event-popup.component";
import {PaymentConfirmationDialogComponent} from './payment-confirmation-dialog/payment-confirmation-dialog.component';
import {PayuButtonComponent} from "./payu-button/payu-button.component";
import { ReturnButtonComponent } from './return-button/return-button.component';

@NgModule({
  declarations: [
    ImagesUploadComponent,
    PaginationComponent,
    EditorComponent,
    CreateCategoryComponent,
    CategorySelectorComponent,
    CategoryFilterComponent,
    CalendarFormComponent,
    AddedEventCardsComponent,
    ConfirmationDialogComponent,
    PaymentConfirmationDialogComponent,
    ReturnButtonComponent
  ],
  exports: [
    ImagesUploadComponent,
    PaginationComponent,
    EditorComponent,
    CreateCategoryComponent,
    CategorySelectorComponent,
    CategoryFilterComponent,
    CalendarFormComponent,
    ReturnButtonComponent
  ],
    imports: [
        CdkDropList,
        CdkDrag,
        NgOptimizedImage,
        GalleryModule,
        CommonModule,
        AngularMaterialModule,
        NgxEditorModule,
        FormsModule,
        ReactiveFormsModule,
        MultiSelectModule,
        AccessDirective,
        MatBadgeModule,
        CalendarComponent,
        NgxMaterialTimepickerModule.setOpts('pl-PL'),
        EditSingleEventPopupComponent,
        PayuButtonComponent
    ],
  providers: []
})
export class SharedComponentsModule {
}
