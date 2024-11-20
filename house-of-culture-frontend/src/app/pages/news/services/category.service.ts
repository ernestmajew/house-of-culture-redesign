import {Injectable} from '@angular/core';
import {
  CategoryApiService,
  CreateCategoryRequestParams
} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  constructor(
    private service: CategoryApiService
  ) {
  }

  createNewCategory(newCategory: CreateCategoryRequestParams) {
    return this.service.createCategory(newCategory);
  }

  getCategories(name?: string) {
    return this.service.findCategoriesByName({name: name});
  }

}
