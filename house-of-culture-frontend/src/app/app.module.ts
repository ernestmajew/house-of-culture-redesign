import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './core/modules/app-routing.module';
import {AppComponent} from './app.component';
import {GeneralLayoutModule} from './core/general-layout/general-layout.module';
import {NewsModule} from './pages/news/news.module';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ManagementModule} from "./pages/administration/management.module";
import {ActivitiesModule} from "./pages/activities/activities.module";
import {EventsModule} from "./pages/events/events.module";
import {BaseComponent} from "./core/abstract-base/base.component";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {Configuration, ConfigurationParameters} from 'out/api/configuration';
import {ApiModule} from 'out/api';
import {MAT_FORM_FIELD_DEFAULT_OPTIONS} from "@angular/material/form-field";
import {TokenInterceptor} from "./core/interceptor/token-interceptor";
import {CalendarDateFormatter, CalendarModule, DateAdapter} from 'angular-calendar';
import {adapterFactory} from 'angular-calendar/date-adapters/date-fns';
import localePl from '@angular/common/locales/pl';
import {LOCALE_ID} from '@angular/core';
import {registerLocaleData} from "@angular/common";
import {NumericDateFormatter} from "./core/util/numeric-date-formatter";
import {environment} from "../environments/environment";

registerLocaleData(localePl);

export function apiConfigFactory(): Configuration {
  const params: ConfigurationParameters = {
    basePath: environment.backendUrl,
  };
  return new Configuration(params);
}

@NgModule({
  declarations: [
    AppComponent,
    BaseComponent,
  ],
  imports: [
    AppRoutingModule,
    BrowserAnimationsModule,
    BrowserModule,
    GeneralLayoutModule,
    ActivitiesModule,
    ManagementModule,
    NewsModule,
    EventsModule,
    HttpClientModule,
    ApiModule.forRoot(apiConfigFactory),
    CalendarModule.forRoot(
      {
        provide: DateAdapter,
        useFactory: adapterFactory
      },
      {
        dateFormatter: {
          provide: CalendarDateFormatter,
          useClass: NumericDateFormatter
        },
      }
      ),
  ],
  providers: [
    {provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: {appearance: 'outline'}},
    {provide: LOCALE_ID, useValue: 'pl'},
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
