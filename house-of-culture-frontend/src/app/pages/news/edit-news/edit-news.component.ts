import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {PostResponseTs} from "out/api/model/post-response";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {NewsService} from "../services/news.service";
import {finalize, switchMap, take} from "rxjs";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {EditPostRequestParams} from "../../../../../out/api";
import {encodeFilesToBase64} from "../../../core/util/encode-image";
import {CategoryResponseTs} from "out/api/model/category-response";

@Component({
  selector: 'app-edit-news',
  templateUrl: './edit-news.component.html',
  styleUrls: ['./edit-news.component.scss']
})
export class EditNewsComponent extends BaseComponent implements OnInit {

  newsToEdit!: PostResponseTs;

  newsForm!: FormGroup;
  imagesFiles?: string[];

  placeholder: string = "Wpisz treść posta tutaj..."
  errorMessage: string = "Treść aktualności jest wymagana."

  apiLoading: boolean = false

  constructor(private route: ActivatedRoute,
              private router: Router,
              private service: NewsService) {
    super();

    this.newsForm = new FormGroup({
      title: new FormControl('', Validators.required),
      editorContent: new FormControl('', Validators.required),
      images: new FormControl([]),
      categories: new FormControl([]),
      createFbPost: new FormControl(false),
      createIgPost: new FormControl(false)
    });
  }

  ngOnInit(): void {
    this.route.params.pipe(
      switchMap((params) => {
        const id = +params['id'];
        return this.service.getNewsById(id);
      }),
      take(1)
    ).subscribe(response => {
      this.newsToEdit = response;
      this.patchForm();
    });
  }

  patchForm() {
    if (this.newsToEdit) {
      this.newsForm.patchValue({
        title: this.newsToEdit.title,
        editorContent: this.newsToEdit.description,
        images: this.newsToEdit.images,
        categories: this.newsToEdit.categories,
        createFbPost: !!this.newsToEdit.fbPostUrl,
        createIgPost: !!this.newsToEdit.igPostUrl
      });

      this.imagesFiles = this.newsToEdit.images.map((image) => {
        return this.imagesBasePath + image;
      });
    }
  }

  onHtmlContentChange(htmlContent: string): void {
    this.newsForm.get("editorContent")?.setValue(htmlContent);
  }

  onChosenCategoriesChange(value: CategoryResponseTs[]): void {
    this.newsForm.get("categories")?.setValue(value);
  }

  onUploadImageChange(files: File[]): void {
    encodeFilesToBase64(files).then((base64Array: any) => {
      this.newsForm.get("images")?.setValue(base64Array)
    });
  }

  editNews(): void {
    let editedNews: EditPostRequestParams = {
      id: this.newsToEdit.id,
      createPostRequestTs: {
        title: this.newsForm.get("title")?.value,
        description: this.newsForm.get("editorContent")?.value,
        categories: this.newsForm.get("categories")?.value,
        images: this.newsForm.get("images")?.value,
        createIgPost: this.newsForm.get("createIgPost")?.value,
        createFbPost: this.newsForm.get("createFbPost")?.value
      }
    }

    this.apiLoading = true
    this.service.editNews(editedNews)
        .pipe(take(1))
        .pipe(finalize(() => {this.apiLoading = false}))
        .subscribe({
          next: () => {
            this.router.navigateByUrl("/news/" + this.newsToEdit.id)
            this.messageService.sendMessage("Post został pomyślnie zapisany.", NotificationMessageType.SUCCESS);
          },
          error: (error) => {
            if(error.status == 500){
              this.messageService.sendMessage(
                "Nie udało się utworzyć postów w mediach społecznościowych.",
                NotificationMessageType.ERROR
              );
            } else {
              this.messageService.sendMessage("Nie udało się zapisać postu.", NotificationMessageType.ERROR);
            }
          },
          complete: () => {this.apiLoading = false}
        })
  }
}
