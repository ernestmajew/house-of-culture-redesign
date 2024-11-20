import {Component, OnInit, ViewChild} from '@angular/core';
import {PostSummaryResponseTs} from "out/api/model/post-summary-response";
import {PaginatedPostResponseTs} from "out/api/model/paginated-post-response"
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {ActivatedRoute, Router} from "@angular/router";
import {CategoryService} from "../services/category.service";
import {take} from "rxjs";
import {CategoryResponseTs} from 'out/api/model/category-response';
import {PaginationComponent} from "../../../core/components/pagination/pagination/pagination.component";

export interface PostResponseTsExtended extends PostSummaryResponseTs {
  imageFile: string | undefined;
}

@Component({
  selector: 'app-news-display',
  templateUrl: './news-display.component.html',
  styleUrls: ['./news-display.component.scss']
})
export class NewsDisplayComponent extends BaseComponent implements OnInit {

  posts?: PaginatedPostResponseTs;
  newsData: PostResponseTsExtended[] = [];
  numberOfPages: number = 0;
  currentPage: number = 1;

  categories: CategoryResponseTs[] = [];
  selectedCategoryId: number | null = null;

  @ViewChild(PaginationComponent) paginationComponent?: PaginationComponent;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private categoryService: CategoryService) {
    super();
  }

  ngOnInit() {
    this.route.data.subscribe(data => {
      if (data['newsData']) {
        this.posts = data['newsData'];
      }

      this.newsData = this.posts!.posts.map((post: PostSummaryResponseTs) => ({
        ...post,
        imageFile: post.image
      }));

      this.numberOfPages = this.posts!.numberOfPages;
      this.paginationComponent?.pageNumberChange(this.posts!.numberOfPages);
    });

    this.categoryService.getCategories().pipe(take(1)).subscribe((response: CategoryResponseTs[]) => {
      this.categories = response;
    });
  }

  fetchNewsByPage(page: number) {
    this.currentPage = page;
    this.parameterChange();
  }

  fetchCategoryFilter(categoryId: number | null) {
    this.selectedCategoryId = categoryId;
    this.currentPage = 1;
    this.paginationComponent?.currentPageChange(1);
    this.parameterChange();
  }

  parameterChange() {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {page: this.currentPage, category: this.selectedCategoryId},
      queryParamsHandling: 'merge'
    });
  }
}
