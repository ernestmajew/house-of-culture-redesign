import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  trigger,
  state,
  style,
  animate,
  transition,
} from '@angular/animations';
import { CategoryResponseTs } from 'out/api/model/category-response';
import { ActivatedRoute } from '@angular/router';
import { take } from 'rxjs';

@Component({
  selector: 'app-category-filter',
  templateUrl: './category-filter.component.html',
  styleUrls: ['./category-filter.component.scss'],
  animations: [
    trigger('selectAnimation', [
      state('show', style({ transform: 'translateX(-120%)', opacity: 1 })),
      state('hide', style({ transform: 'translateX(-90%)', opacity: 0 })),
      transition('show => hide', animate('0.4s ease')),
      transition('hide => show', animate('0.4s ease')),
    ]),
  ],
})
export class CategoryFilterComponent implements OnInit {
  @Input() categories?: CategoryResponseTs[];
  @Output() filterCategory = new EventEmitter<number | null>();
  selectedCategory: number | null = null;

  showSelect = true;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.pipe(take(1)).subscribe((params) => {
      const category = params['category'];
      if (category) {
        this.selectedCategory = +category;
        this.showSelect = false;
      }
    });
  }

  filterByCategory() {
    this.filterCategory.emit(this.selectedCategory);
  }
}
