import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from "@angular/router";
import {MatButtonModule} from "@angular/material/button";

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.scss']
})
export class PaymentComponent{

  constructor(
    private router: Router,
  ) {
  }

  // ngOnInit(): void {
  //   this.activatedRoute.params.subscribe(params => {
  //     const uuid = params['uuid']
  //     if(uuid){
  //     }
  //   });
  // }

  goToMyActivities() {
    this.router.navigate(['/my-activities']);
  }
}
