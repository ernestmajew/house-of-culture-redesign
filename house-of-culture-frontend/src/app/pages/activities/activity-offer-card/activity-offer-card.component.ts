import {Component, Input, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ActivitySummaryResponseTs} from "../../../../../out/api";
import {BaseComponent} from "../../../core/abstract-base/base.component";

@Component({
  selector: 'app-activity-offer-card',
  templateUrl: './activity-offer-card.component.html',
  styleUrls: ['./activity-offer-card.component.scss']
})
export class ActivityOfferCardComponent extends BaseComponent implements OnInit {
  @Input() activity!: ActivitySummaryResponseTs;
  activityMainPicture?: string;

  constructor(private router: Router) {
    super();
  }

  ngOnInit(): void {
    if (this.activity.image){
      this.activityMainPicture = this.imagesBasePath + this.activity.image;
    }
  }


  navigateForMore() {
    this.router.navigateByUrl("/activities/" + this.activity.id)
  }
}
