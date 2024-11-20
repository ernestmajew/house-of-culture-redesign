import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit {

  @Input() numberOfPages = 0;
  @Output() currentPageEmitter: EventEmitter<number> = new EventEmitter<number>();

  currentPage: number = 1;
  totalPages: any[] = [];

  constructor() {
  }

  public currentPageChange(page: number) {
    this.currentPage = page;
  }

  public pageNumberChange(pageNumber: number){
    this.numberOfPages = pageNumber;
    this.calculateTotalPageNumber();
  }

  ngOnInit(): void {
    this.calculateTotalPageNumber();
  }

  calculateTotalPageNumber(): void{
    this.totalPages = new Array(Math.ceil(this.numberOfPages));
  }

  selectPageNumber(pageNumber: number) {
    this.currentPage = pageNumber;
    this.currentPageEmitter.emit(this.currentPage);
  }

  next() {
    const nextPage = this.currentPage + 1;
    nextPage <= this.totalPages.length && this.selectPageNumber(nextPage);
  }

  previous() {
    const previousPage = this.currentPage - 1;
    previousPage >= 1 && this.selectPageNumber(previousPage);
  }

  goToFirstPage() {
    this.selectPageNumber(1);
  }

  goToLastPage() {
    this.selectPageNumber(this.totalPages.length);
  }

  get visiblePageNumbers(): number[] {
    const maxButtons = 3;

    if (this.totalPages.length <= maxButtons) {
      return Array.from({length: this.totalPages.length}, (_: any, i: number) => i + 1);
    } else if (this.currentPage < 2) {
      return Array.from({length: 2}, (_: any, i: number) => i + 1);
    } else if (this.currentPage == this.totalPages.length) {
      return Array.from({length: 2}, (_: any, i: number) => i + this.totalPages.length - 1);
    } else {
      return Array.from({length: 3}, (_: any, i: number) => i + this.currentPage - 1);
    }
  }
}
