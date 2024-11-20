import {Component, EventEmitter, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-payu-button',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './payu-button.component.html',
  styleUrls: ['./payu-button.component.scss']
})
export class PayuButtonComponent {
  @Output() click = new EventEmitter<void>()

  handleClick() {
    this.click.emit()
  }
}
