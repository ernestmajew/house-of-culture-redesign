import {Component} from '@angular/core';
import {BaseComponent} from "../abstract-base/base.component";
import {LoadingService} from "../services/loading.service";

@Component({
  selector: 'app-general-layout',
  templateUrl: './general-layout.component.html',
  styleUrls: ['./general-layout.component.scss']
})
export class GeneralLayoutComponent extends BaseComponent {
  loading = this.loadingService.loading;

  constructor(private loadingService: LoadingService) {
    super();
  }

}
