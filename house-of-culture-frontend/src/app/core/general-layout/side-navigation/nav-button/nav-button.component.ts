import { Component, Input } from '@angular/core';
import { UserRoleTs } from 'out/api';

@Component({
  selector: 'app-nav-button',
  templateUrl: './nav-button.component.html',
  styleUrls: ['./nav-button.component.scss'],
})
export class NavButtonComponent {
  @Input() icon!: string;
  @Input() route!: string;
  @Input() label!: string;
  @Input() role!: UserRoleTs;

  isButtonActive(route: string): boolean {
    return window.location.pathname.startsWith(route);
  }
}
