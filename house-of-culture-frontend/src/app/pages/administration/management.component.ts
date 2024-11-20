import {Component} from '@angular/core';
import {BaseComponent} from "../../core/abstract-base/base.component";
import {Router} from "@angular/router";
import {UserRoleTs} from "../../../../out/api";

export interface AdminPanelFunction {
  name: string;
  icon: string;
  route: string;
  visibleOnlyToAdmin?: boolean;
}

@Component({
  selector: 'app-administration',
  templateUrl: './management.component.html',
  styleUrls: ['./management.component.scss']
})
export class ManagementComponent extends BaseComponent {
  createAdminPanelFunctions: AdminPanelFunction[] = [
    {
      name: "Użytkownika",
      icon: "person_add",
      route: "admin/add-person",
      visibleOnlyToAdmin: true
    },
    {
      name: "Post",
      icon: "feedback",
      route: "admin/new-news",
      visibleOnlyToAdmin: false
    },
    {
      name: "Zajęcia",
      icon: "extension",
      route: "admin/new-activity",
      visibleOnlyToAdmin: false
    }
    ];

  updateAdminPanelFunctions: AdminPanelFunction[] = [
    {
      name: "Dane kontaktowe",
      icon: "phone",
      route: "admin/contact-info",
      visibleOnlyToAdmin: true
    },
    {
      name: "Media społecznościowe",
      icon: "facebook-box",
      route: "admin/social-media",
      visibleOnlyToAdmin: true
    }
    ];
  constructor(
    private router: Router,
  ) {
    super();
  }

  goTo(route: string) {
    this.router.navigate([`manage/${route}`]);
  }

  protected readonly UserRoleTs = UserRoleTs;
}
