import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {ActivityInfoCardComponent} from "./activity-info-card/activity-info-card.component";
import {NgForOf, NgIf, NgStyle} from "@angular/common";
import {MatDividerModule} from "@angular/material/divider";
import {ActivityForUserTs} from "../../../../../out/api";
import {AngularMaterialModule} from "../../../core/modules/angular-material.module";
import {Router} from "@angular/router";

@Component({
  selector: 'app-my-activity-list',
  templateUrl: './my-activity-list.component.html',
  styleUrls: ['./my-activity-list.component.scss'],
  standalone: true,
  imports: [
    NgForOf,
    ActivityInfoCardComponent,
    MatDividerModule,
    NgStyle,
    AngularMaterialModule,
    NgIf
  ]
})
export class MyActivityListComponent implements OnInit, OnChanges{
  @Input() activities: ActivityForUserTs[] = []

  isScrollable: boolean = false;


  constructor(
    private router: Router
  ) {}

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.isScrollable = this.activities?.length > 5;
  }

  navigateToPeriodicPayment() {
    this.router.navigateByUrl("my-activities/payment/periodic")
  }
}
