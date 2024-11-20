import {Component, OnDestroy, OnInit} from '@angular/core';
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {ActivatedRoute, Router} from "@angular/router";
import {PostResponseTs} from "out/api/model/post-response";
import {NewsService} from "../services/news.service";
import {take} from "rxjs";
import {DomSanitizer} from "@angular/platform-browser";
import {UserRoleTs} from "../../../../../out/api";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {ImageItem} from "ng-gallery";

@Component({
  selector: 'app-news-detail',
  templateUrl: './news-detail.component.html',
  styleUrls: ['./news-detail.component.scss']
})
export class NewsDetailComponent extends BaseComponent implements OnInit, OnDestroy {

  news!: PostResponseTs;
  images: ImageItem[] = [];
  postInfo!: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private newsService: NewsService,
              private sanitizer: DomSanitizer) {
    super();
  }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get('id') as unknown as number;

    this.newsService.getNewsById(id).pipe(take(1)).subscribe({
      next: (response: PostResponseTs) => {
        this.news = response;
        this.postInfo = this.news.author.firstName + " " + this.news.author.lastName + " - " + new Date(this.news.created_at).toLocaleDateString();

        if (this.news.images.length > 0) {
          this.images = this.news.images?.map((url) => {
            return new ImageItem({
              src: `${this.imagesBasePath}${url}`,
              thumb: `${this.imagesBasePath}${url}`
            })
        })
        } else {
          this.images = [new ImageItem({src: "/assets/noImage.jpg", thumb: "/assets/noImage.jpg"})]
        }
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  navigateForFb() {
    if(this.news.fbPostUrl)
      window.open(this.news.fbPostUrl, "_blank")
  }

  navigateForIg() {
    if(this.news.igPostUrl)
      window.open(this.news.igPostUrl, "_blank")
  }

  sanitizeDescription(html: string): any {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

  deletePost() {
    this.newsService.deleteNews(this.news.id)
      .pipe(take(1))
      .subscribe((response) => {
          this.router.navigateByUrl("/news")

          if(response.status == 202) {
            this.messageService.sendMessage(
              "Post został usunięty, jednak nie udało się usunąć postów w mediach społecznościowych.",
              NotificationMessageType.WARNING
            )
          } else {
            this.messageService.sendMessage("Pomyślnie usunięto post", NotificationMessageType.SUCCESS)
          }
        }
    )
  }

  editPost() {
    this.router.navigateByUrl("manage/admin/edit-news/" + this.news.id);
  }

  protected readonly UserRoleTs = UserRoleTs;
}
