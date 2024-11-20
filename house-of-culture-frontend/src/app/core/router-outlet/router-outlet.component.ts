import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-router-outlet',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './router-outlet.component.html',
  styleUrls: ['./router-outlet.component.scss']
})
export class RouterOutletComponent {

}
