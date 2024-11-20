import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ActivitySummaryResponseTs, CategoryResponseTs} from "../../../../../out/api";
import {PaginationComponent} from "../../../core/components/pagination/pagination/pagination.component";
import {DomSanitizer} from "@angular/platform-browser";
import {LoadingService} from "../../../core/services/loading.service";

@Component({
  selector: 'app-activities-offers',
  templateUrl: './activities-offers.component.html',
  styleUrls: ['./activities-offers.component.scss']
})
export class ActivitiesOffersComponent implements OnInit {
  activities: ActivitySummaryResponseTs[] = [];
  categories: CategoryResponseTs[] = [];
  numberOfPages: number = 1;
  currentPage: number = 1;
  selectedCategoryId: number | null = null;
  searchText: string = '';
  @ViewChild(PaginationComponent) paginationComponent?: PaginationComponent;

  constructor(
    private route: ActivatedRoute,
              private router: Router,
              private sanitizer: DomSanitizer,
    private loadingService: LoadingService,
  ) {}

  ngOnInit() {
    this.loadingService.setLoadingTrue()
    this.route.data.subscribe(
      ({categories, activities}) => {
        this.categories = categories
        this.activities = activities.items
        this.numberOfPages = activities.pages
        this.paginationComponent?.pageNumberChange(activities.pages);

        this.loadingService.setLoadingFalse()
      });
    this.route.queryParams
      .subscribe(params => {
        this.currentPage = params['page'] ?? 1;
        this.selectedCategoryId = params['category'] ?? null;
        this.searchText = params['searchText'] ?? '';
        this.parameterChange();
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

  searchByText() {
    const sanitizedText = this.sanitizer.sanitize(0, this.searchText);
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {searchText: sanitizedText, page: this.currentPage, category: this.selectedCategoryId},
      queryParamsHandling: 'merge'
    });

    this.searchText = '';
  }

  clearSearchText() {
    this.searchText = '';
    this.searchByText();
  }

  backToPreviousPage() {
    this.router.navigateByUrl("/activities")
  }
}
