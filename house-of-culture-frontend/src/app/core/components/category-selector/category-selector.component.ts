import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BaseComponent} from "../../abstract-base/base.component";
import {MatDialog} from "@angular/material/dialog";
import {CreateCategoryComponent} from "./create-category/create-category.component";
import {CategoryResponseTs} from "out/api/model/category-response";
import {CategoryService} from "../../../pages/news/services/category.service";
import {take} from "rxjs";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-category-selector',
  templateUrl: './category-selector.component.html',
  styleUrls: ['./category-selector.component.scss']
})
export class CategorySelectorComponent extends BaseComponent implements OnInit {
  categoryList: CategoryResponseTs[] = [];
  groupForm: FormGroup;

  @Input() categories?: CategoryResponseTs[];
  @Output() updateCategoriesControl: EventEmitter<CategoryResponseTs[]> = new EventEmitter<CategoryResponseTs[]>();

  constructor(private dialog: MatDialog,
              private service: CategoryService,
              private fb: FormBuilder) {
    super();

    this.groupForm = this.fb.group({
      selectedCategories: [[]],
    });
  }

  ngOnInit(): void {
    if (this.categories) {
      let ids = this.categories.map(value => value.id)
      this.groupForm.get("selectedCategories")?.setValue([...ids]);
      this.selectedCategoriesChange();
    }

    this.service.getCategories()
      .pipe(take(1))
      .subscribe(categories => {
        this.categoryList = categories;
      })
  }

  get pixelHeightSize(): string {
    const baseHeight = 55;
    const multiplier = Math.floor(this.groupForm.get("selectedCategories")!.value.length / 5); // Fix the typo here
    const extra = multiplier * 50;
    return (baseHeight + extra) + 'px';
  }

  selectedCategoriesChange(){
    this.updateCategoriesControl.emit(this.groupForm.get("selectedCategories")?.value);
  }

  openCreateCategoryDialog(): void {
    const dialogRef = this.dialog.open(CreateCategoryComponent, {
      width: '30rem',
      height: '15rem',
      autoFocus: 'dialog',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.categoryList.push(result);
      }
    });
  }
}
