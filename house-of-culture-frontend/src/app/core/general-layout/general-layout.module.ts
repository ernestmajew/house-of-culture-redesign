import {NgModule} from '@angular/core';
import {SideNavigationComponent} from './side-navigation/side-navigation.component';
import {ToolbarComponent} from './toolbar/toolbar.component';
import {GeneralLayoutComponent} from "./general-layout.component";
import {RouterModule} from "@angular/router";
import {NotificationMessageModule} from "../notification-message/notification-message.module";
import {AngularMaterialModule} from "../modules/angular-material.module";
import {AsyncPipe, NgIf} from "@angular/common";
import {AuthButtonsComponent} from "../../pages/auth/auth-buttons/auth-buttons.component";
import {AccessDirective} from "../guards/directive/access.directive";
import {MatProgressBarModule} from "@angular/material/progress-bar";

@NgModule({
  declarations: [
    GeneralLayoutComponent,
    SideNavigationComponent,
    ToolbarComponent
  ],
    imports: [
        AngularMaterialModule,
        NotificationMessageModule,
        RouterModule,
        NgIf,
        AsyncPipe,
        AuthButtonsComponent,
        AccessDirective,
        MatProgressBarModule
    ],
  exports: [GeneralLayoutComponent]
})
export class GeneralLayoutModule {}
