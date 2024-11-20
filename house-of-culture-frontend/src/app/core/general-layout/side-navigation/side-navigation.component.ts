import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {ContactInfoTs} from "out/api/model/contact-info";
import {JwtTokenService} from "../../../pages/auth/services/jwt-token.service";
import {ContactInfoService} from "../../../pages/administration/services/contact-info.service";
import {UserRoleTs} from "../../../../../out/api";
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-side-navigation',
  templateUrl: './side-navigation.component.html',
  styleUrls: ['./side-navigation.component.scss']
})
export class SideNavigationComponent implements OnInit {
  contactInfo?: Observable<ContactInfoTs>;

  constructor(private service: ContactInfoService,
              private jwtTokenService: JwtTokenService,
              private router: Router,
              private route: ActivatedRoute
              ) {}

  ngOnInit(): void {
    this.contactInfo = this.getContactInfo();
  }

  getContactInfo() {
    return this.service.getContactInfo()
  }

  isButtonActive(routePath: string): boolean {
    return this.router.isActive(routePath, true) && this.route.snapshot.fragment === '';
  }

  protected readonly UserRoleTs = UserRoleTs;
}
