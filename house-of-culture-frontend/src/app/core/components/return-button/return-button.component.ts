import {Component, Input} from '@angular/core';
import {NavigationService} from "../../services/navigation.service";

@Component({
  selector: 'app-return-button',
  templateUrl: './return-button.component.html',
  styleUrls: ['./return-button.component.scss']
})
export class ReturnButtonComponent {
  @Input() text!: string

  constructor(private navigation: NavigationService) {
  }
  backToPreviousPage() {
    this.navigation.back();
  }
}
