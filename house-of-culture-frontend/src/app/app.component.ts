import {Component} from '@angular/core';
import {environment} from "../environments/environment";
import {Title} from "@angular/platform-browser";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent{
  houseOfCultureName = environment.institutionName

  constructor(
    private titleService: Title
  ) {
    this.titleService.setTitle(this.houseOfCultureName);
  }
}
