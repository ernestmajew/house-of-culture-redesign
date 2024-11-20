import {NgModule} from '@angular/core';
import {provideRouter, Route, RouterModule, withComponentInputBinding} from '@angular/router';
import {ActivitiesOffersComponent} from "../../pages/activities/activities-offers/activities-offers.component";
import {MyActivitiesComponent} from "../../pages/activities/my-activities/my-activities.component";
import {ManagementComponent} from "../../pages/administration/management.component";
import {ActivitiesComponent} from "../../pages/activities/activities.component";
import {ContactInfoFormComponent} from "../../pages/administration/contact-info-form/contact-info-form.component";
import {LoginFormComponent} from "../../pages/auth/login-form/login-form.component";
import {RegisterComponent} from "../../pages/auth/register/register.component";
import {MyAccountComponent} from "../../pages/my-account/my-account.component";
import {PasswordChangeComponent} from "../../pages/auth/password-change/password-change.component";
import {AddChildComponent} from "../../pages/my-account/add-child/add-child.component";
import {AddPersonComponent} from "../../pages/administration/add-person/add-person.component";
import {RouterOutletComponent} from "../router-outlet/router-outlet.component";
import {NewsDetailComponent} from "../../pages/news/news-detail/news-detail.component";
import {unloggedUserGuard} from "../guards/unlogged-user.guard";
import {loggedUserGuard} from "../guards/logged-user.guard";
import {UserRoleTs} from "../../../../out/api";
import {userRoleGuard} from "../guards/user-based.guard";
import {NewsDisplayComponent} from "../../pages/news/news-display/news-display.component";
import {NewsDataResolver} from "../../pages/news/news-display/reslovers/news.resolver";
import {EditNewsComponent} from "../../pages/news/edit-news/edit-news.component";
import {SocialMediaComponent} from "../../pages/administration/social-media/social-media.component";
import {categoryResolver} from "../../pages/activities/resolvers/category.resolver";
import {activitiesResolver} from "../../pages/activities/resolvers/activities.resolver";
import {ActivityDetailComponent} from "../../pages/activities/activity-detail/activity-detail.component";
import {
  NewActivitiesFormComponent
} from "../../pages/administration/create-new-activities/new-activities-form.component";
import {NewNewsFormComponent} from "../../pages/administration/new-news-form/new-news-form.component";
import {instructorsResolver} from "../../pages/activities/resolvers/instructors.resolver";
import {EditActivitiesComponent} from "../../pages/activities/edit-activities/edit-activities.component";
import {ActivityEnrollComponent} from "../../pages/activities/activity-enroll/activity-enroll.component";
import {InstructorComponent} from "../../pages/instructor/instructor.component";
import {PaymentComponent} from "../../pages/payment/payment-continue/payment.component";
import {ActivityPaymentsComponent} from "../../pages/activities/activity-payments/activity-payments.component";
import {activityPaymentsResolver} from "../../pages/activities/resolvers/activity-payments.resolver";
import {
  ActivityPeriodicPaymentComponent
} from "../../pages/activities/activity-periodic-payment/activity-periodic-payment.component";
import {loggedInUserResolver} from "../../pages/activities/resolvers/logged-in-user.resolver";
import {userChildrenResolver} from "../../pages/activities/resolvers/user-children.resolver";
import {EnrolledUserListComponent} from "../../pages/activities/enrolled-user-list/enrolled-user-list.component";

type RouteKey =
  "news"
  | "singleNews"
  | "activities"
  | "admin"
  | "login"
  | "register"
  | "account"
  | "passwordChange"
  | "payment"
  | "instructor";

const ROUTES: Record<RouteKey, Route> = {
  news: {
    path: "news",
    component: NewsDisplayComponent,
    resolve: {
      newsData: NewsDataResolver
    },
    runGuardsAndResolvers: 'paramsOrQueryParamsChange'
  },
  singleNews: {
    path: 'news/:id',
    component: NewsDetailComponent,
  },
  instructor: {
    path: 'instructor',
    component: InstructorComponent
  },
  activities: {
    path: "",
    component: RouterOutletComponent,
    children: [
      {
        path: "activities",
        component: ActivitiesComponent
      },
      {
        path: "activities/offers",
        component: ActivitiesOffersComponent,
        resolve: {
          categories: categoryResolver,
          activities: activitiesResolver
        },
        runGuardsAndResolvers: 'paramsOrQueryParamsChange'
      },
      {
        path: "my-activities",
        component: MyActivitiesComponent,
        canActivate: [loggedUserGuard],
      },
      {
        path: "my-activities/:eventId/payment/:userId",
        component: ActivityPaymentsComponent,
        canActivate: [userRoleGuard(UserRoleTs.CLIENT)],
        resolve: {
          payments: activityPaymentsResolver
        }
      },
      {
        path: "my-activities/payment/periodic",
        component: ActivityPeriodicPaymentComponent,
        canActivate: [userRoleGuard(UserRoleTs.CLIENT)],
        resolve: {
          loggedInUser: loggedInUserResolver,
          children: userChildrenResolver
        }
      },
      {
        path: "activities/:id",
        component: ActivityDetailComponent
      },
      {
        path: "activities/enroll/:id",
        component: ActivityEnrollComponent,
        canActivate: [userRoleGuard(UserRoleTs.CLIENT)]
      },
      {
        path: "activities/enrollment/instructor/:id",
        component: EnrolledUserListComponent,
        canActivate: [userRoleGuard(UserRoleTs.INSTRUCTOR)],
        data: { instructor: true }
      },
      {
        path: "activities/enrollment/:id",
        component: EnrolledUserListComponent,
        canActivate: [userRoleGuard(UserRoleTs.EMPLOYEE)],
        data: { instructor: false }
      }
    ]
  },
  payment: {
    path: "",
    component: RouterOutletComponent,
    children: [
      {
        path: "payment/continue/:uuid",
        component: PaymentComponent,
      }
    ]
  },
  login: {
    path: "login",
    component: LoginFormComponent,
    canActivate: [unloggedUserGuard]
  },
  register: {
    path: "register",
    component: RegisterComponent,
    canActivate: [unloggedUserGuard]
  },
  account: {
    path: "",
    component: RouterOutletComponent,
    canActivateChild: [loggedUserGuard],
    children: [
      {
        path: "account",
        component: MyAccountComponent,
      },
      {
        path: "account/add-child",
        component: AddChildComponent,
      }
    ]
  },
  passwordChange: {
    path: "password-change/:uuid",
    component: PasswordChangeComponent
  },
  admin: {
    path: "",
    component: RouterOutletComponent,
    canActivateChild: [userRoleGuard(UserRoleTs.EMPLOYEE)],
    children: [
      {
        path: "manage",
        component: ManagementComponent
      },
      {
        path: "manage/admin",
        canActivate: [userRoleGuard(UserRoleTs.EMPLOYEE)],
        children: [
          {
            path: "",
            redirectTo: "/manage",
            pathMatch: "full"
          },
          {
            path: "contact-info",
            component: ContactInfoFormComponent,
            canActivate: [userRoleGuard(UserRoleTs.ADMIN)]
          },
          {
            path: "add-person",
            component: AddPersonComponent,
            canActivate: [userRoleGuard(UserRoleTs.ADMIN)]
          },
          {
            path: "new-news",
            component: NewNewsFormComponent
          },
          {
            path: "edit-news/:id",
            component: EditNewsComponent
          },
          {
            path: "social-media",
            component: SocialMediaComponent
          },
          {
            path: "new-activity",
            component: NewActivitiesFormComponent,
            resolve: {
              instructors: instructorsResolver
            },
          },
          {
            path: "edit-activity/:id",
            component: EditActivitiesComponent,
            resolve: {
              instructors: instructorsResolver
            },
          },
        ]
      }
    ]
  }
};


@NgModule({
  imports: [
    RouterModule.forRoot([
      {
        path: "",
        pathMatch: "full",
        redirectTo: "news"
      },
      {
        path: "",
        children: Object.values(ROUTES)
      },
      {
        path: "**",
        pathMatch: "full",
        redirectTo: "news"
      }
    ])
  ],
  exports: [RouterModule],
  providers: [
    provideRouter(Object.values(ROUTES), withComponentInputBinding())
  ]
})
export class AppRoutingModule {
}
