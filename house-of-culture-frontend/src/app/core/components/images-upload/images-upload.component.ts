import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CdkDragDrop, moveItemInArray} from "@angular/cdk/drag-drop";
import {ImageService} from "../../../pages/news/services/image.service";
import {BaseComponent} from "../../abstract-base/base.component";
import {forkJoin} from "rxjs";

@Component({
  selector: 'app-images-upload',
  templateUrl: './images-upload.component.html',
  styleUrls: ['./images-upload.component.scss']
})
export class ImagesUploadComponent extends BaseComponent implements OnInit{
  selectedImages: File[] = [];
  selectedImagesUrls: string[] = [];
  fileReader = new FileReader();

  @Input() givenImage?: string[];
  @Output() selectImagesEvent = new EventEmitter<File[]>();

  constructor(private imageService: ImageService) {
    super();
  }

  ngOnInit(): void {
    if (this.givenImage) {
      this.selectedImagesUrls = this.givenImage;

      const observables = this.givenImage.map(url =>
        this.imageService.getImage(url)
      );

      forkJoin(observables).subscribe(blobDataArray => {
        blobDataArray.forEach(blobData => {
          const file = new File([blobData], 'image.png');
          this.selectedImages.push(file);
        });

        this.emitOutputEvent();
      });
    }

    this.fileReader.onload = (event) => {
      this.selectedImagesUrls.push(event.target!.result as string);
    };
  }


  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.selectedImagesUrls, event.previousIndex, event.currentIndex);
    moveItemInArray(this.selectedImages, event.previousIndex, event.currentIndex);
    this.emitOutputEvent();
  }

  onImageSelect(event: Event): void {
    const files = (event.target as HTMLInputElement).files as FileList;

    Array.from(files).forEach(file => {
        this.fileReader.readAsDataURL(file);
        this.selectedImages.push(file);
      }
    );

    this.emitOutputEvent();
  }

  deleteImage(index: number): void {
    this.selectedImages.splice(index, 1);
    this.selectedImagesUrls.splice(index, 1);
    this.emitOutputEvent();
  }

  emitOutputEvent(): void {
    this.selectImagesEvent.emit(this.selectedImages)
  }
}
