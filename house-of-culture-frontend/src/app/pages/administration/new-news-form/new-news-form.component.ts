import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {NewsService} from "../../news/services/news.service";
import {CreatePostRequestParams} from "../../../../../out/api";
import {finalize, take} from "rxjs";
import {BaseComponent} from "../../../core/abstract-base/base.component";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {CategoryResponseTs} from "out/api/model/category-response";
import {encodeFilesToBase64} from "../../../core/util/encode-image";

@Component({
  selector: 'app-new-news-form',
  templateUrl: './new-news-form.component.html',
  styleUrls: ['./new-news-form.component.scss']
})
export class NewNewsFormComponent extends BaseComponent {
  newsForm: FormGroup = new FormGroup({
    title: new FormControl('', Validators.required),
    editorContent: new FormControl('', Validators.required),
    images: new FormControl([]),
    categories: new FormControl([]),
    createFbPost: new FormControl(false),
    createIgPost: new FormControl(false)
  });

  placeholder: string = "Wpisz treść posta tutaj..."
  errorMessage: string = "Treść aktualności jest wymagana."

  apiLoading: boolean = false

  constructor(private router: Router,
              private service: NewsService) {
    super();
  }

  onHtmlContentChange(htmlContent: string): void {
    this.newsForm.get("editorContent")?.setValue(htmlContent);
  }

  onChosenCategoriesChange(value: CategoryResponseTs[]): void {
    this.newsForm.get("categories")?.setValue(value);
  }

  onUploadImageChange(files: File[]): void {
    encodeFilesToBase64(files).then((base64Array) => {
      this.newsForm.get("images")?.setValue(base64Array)
    });
  }

  createNewNews(): void {
    let newNews: CreatePostRequestParams = {
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
    this.service.createNewNews(newNews)
      .pipe(take(1))
      .pipe(finalize(() => {this.apiLoading = false}))
      .subscribe({
        next: () => {
          this.router.navigateByUrl("/manage")
          this.messageService.sendMessage("Nowy post został dodany", NotificationMessageType.SUCCESS);
        },
        error: (error) => {
          switch (error.status) {
            case 422:
              this.messageService.sendMessage(
                "Wybrano nieprawidłowy format zdjęć, lub nieprawidłowe kategorie, spróbuj ponownie.",
                NotificationMessageType.ERROR
              );
              break;
            case 500:
              this.messageService.sendMessage(
                "Nie udało się utworzyć postów w mediach społecznościowych. Post nie został utworzony.",
                NotificationMessageType.ERROR
              );
              break;
            default:
              this.messageService.sendMessage(
                "Wystąpił nieoczekiwany błąd, spróbuj ponownie póżniej.",
                NotificationMessageType.ERROR
              );
              break;
          }
        }
      })
  }
}
