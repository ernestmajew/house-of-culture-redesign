import {NgModule} from "@angular/core";
import {ManagementComponent} from './management.component';
import {AppRoutingModule} from "../../core/modules/app-routing.module";
import {AngularMaterialModule} from "../../core/modules/angular-material.module";
import {ContactInfoFormComponent} from "./contact-info-form/contact-info-form.component";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AsyncPipe, NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {SharedComponentsModule} from "../../core/components/shared-components.module";
import {MatListModule} from "@angular/material/list";
import {MatCardModule} from "@angular/material/card";
import {AccessDirective} from "../../core/guards/directive/access.directive";
import {SocialMediaComponent} from './social-media/social-media.component';
import {UserCodeDialogComponent} from './social-media/user-code-dialog/user-code-dialog.component';
import {MatDialogModule} from "@angular/material/dialog";
import {PageConnectionDialogComponent} from './social-media/page-connection-dialog/page-connection-dialog.component';
import {ClipboardModule} from "@angular/cdk/clipboard";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {NewActivitiesFormComponent} from "./create-new-activities/new-activities-form.component";
import {NewNewsFormComponent} from "./new-news-form/new-news-form.component";
import {FullnamePipe} from "../../core/pipes/fullname.pipe";

@NgModule({
  declarations: [
    ManagementComponent,
    ContactInfoFormComponent,
    SocialMediaComponent,
    UserCodeDialogComponent,
    PageConnectionDialogComponent,
    NewActivitiesFormComponent,
    NewNewsFormComponent
  ],
  imports: [
    AngularMaterialModule,
    AppRoutingModule,
    NgOptimizedImage,
    ReactiveFormsModule,
    AsyncPipe,
    SharedComponentsModule,
    MatListModule,
    MatCardModule,
    NgForOf,
    AccessDirective,
    MatDialogModule,
    NgIf,
    ClipboardModule,
    MatCheckboxModule,
    FormsModule,
    FullnamePipe
  ],
  exports: [
    ManagementComponent,
    ContactInfoFormComponent
  ],
  providers: [
    FullnamePipe
  ]
})
export class ManagementModule {
}
