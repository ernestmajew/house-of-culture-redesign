import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve} from '@angular/router';
import {PaginatedPostResponseTs} from "out/api/model/paginated-post-response";
import {take} from 'rxjs/operators';
import {NewsService} from "../../services/news.service";

@Injectable({
  providedIn: 'root'
})
export class NewsDataResolver implements Resolve<PaginatedPostResponseTs> {
  constructor(private newsService: NewsService) {
  }

  resolve(route: ActivatedRouteSnapshot) {
    const customPage = route.queryParams['page'] || 1
    const categoryId = route.queryParams['category']

    if (categoryId == null) {
      return this.newsService.getAllNews({
        paginationParams: {
          page: customPage - 1,
          pageSize: 6
        }
      }).pipe(take(1));
    } else {
      return this.newsService.getAllNews({
        paginationParams: {
          page: customPage - 1,
          pageSize: 6
        },
        categoryId: categoryId
      }).pipe(take(1));
    }
  }
}
