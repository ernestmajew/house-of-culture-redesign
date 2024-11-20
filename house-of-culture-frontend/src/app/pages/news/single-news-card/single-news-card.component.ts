import {Component, Input, OnInit} from '@angular/core';
import {PostSummaryResponseTs} from "out/api/model/post-summary-response";
import {Router} from "@angular/router";
import {BaseComponent} from "../../../core/abstract-base/base.component";

@Component({
  selector: 'app-single-news-card',
  templateUrl: './single-news-card.component.html',
  styleUrls: ['./single-news-card.component.scss']
})
export class SingleNewsCardComponent extends BaseComponent implements OnInit{

  @Input() news!: PostSummaryResponseTs;
  newsMainPicture!: string;
  normalizedDate: string = "";

  constructor(private router: Router) {
    super();
  }
  ngOnInit(): void {
    this.normalizedDate = new Date(this.news.created_at).toLocaleDateString();
    if (this.news.image){
      this.newsMainPicture = this.imagesBasePath + this.news.image;
    } else {
      this.newsMainPicture = "assets/noImage.jpg"
    }
  }

  navigateForMore() {
    this.router.navigate(["/news/" + this.news.id])
  }

  navigateForFb() {
    window.open(this.news.fbPostUrl, "_blank")
  }

  navigateForIg() {
    window.open(this.news.igPostUrl!, "_blank")
  }
}

