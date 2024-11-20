import {Component} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../abstract-base/base.component";
import {NotificationMessageType} from "../../../models/notification-message";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CategoryService} from "../../../../pages/news/services/category.service";
import {CategoryResponseTs} from "out/api/model/category-response";
import {take} from "rxjs";

@Component({
  selector: 'app-create-category',
  templateUrl: './create-category.component.html',
  styleUrls: ['./create-category.component.scss']
})
export class CreateCategoryComponent extends BaseComponent {
  categoryForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<CreateCategoryComponent>,
              private fb: FormBuilder,
              private service: CategoryService) {
    super();
    this.categoryForm = this.fb.group({
      categoryName: ['', [Validators.required]],
    });
  }

  createCategory() {
    if (this.categoryForm.valid) {
      let categoryName = this.categoryForm.get('categoryName')!.value;
      this.service.createNewCategory({createCategoryRequestTs: {name: categoryName}})
        .pipe(take(1))
        .subscribe({
          next: (category: CategoryResponseTs) => {
            this.dialogRef.close({id: category.id, name: category.name});
            this.messageService.sendMessage(
              "Dodano nową kategorię: " + category.name + ". Możesz ją teraz wybrać.",
              NotificationMessageType.SUCCESS
            )
          },
          error: () => {
            this.dialogRef.close();
            this.messageService.sendMessage(
              "Taka kategoria już istnieje.",
              NotificationMessageType.ERROR
            )
          }
        })
    }
  }

  cancel() {
    this.dialogRef.close();
  }
}
