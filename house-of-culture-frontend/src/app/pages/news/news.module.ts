import {NgModule} from "@angular/core";
import {NewsComponent} from './news.component';
import {AppRoutingModule} from "../../core/modules/app-routing.module";
import {AngularMaterialModule} from "../../core/modules/angular-material.module";
import {SingleNewsCardComponent} from './single-news-card/single-news-card.component';
import {NewsDisplayComponent} from './news-display/news-display.component';
import {CommonModule} from "@angular/common";
import {NewsDetailComponent} from './news-detail/news-detail.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {SharedComponentsModule} from "../../core/components/shared-components.module";
import {AccessDirective} from "../../core/guards/directive/access.directive";
import {EditNewsComponent} from './edit-news/edit-news.component';
import {GallerizeDirective} from "ng-gallery/lightbox";
import {GalleryComponent} from "ng-gallery";

@NgModule({
  declarations: [
    NewsComponent,
    SingleNewsCardComponent,
    NewsDisplayComponent,
    NewsDetailComponent,
    EditNewsComponent
  ],
    imports: [
        AngularMaterialModule,
        AppRoutingModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        SharedComponentsModule,
        AccessDirective,
        GallerizeDirective,
        GalleryComponent
    ],
  exports: [
    NewsComponent
  ],
  providers: []
})
export class NewsModule {
}
