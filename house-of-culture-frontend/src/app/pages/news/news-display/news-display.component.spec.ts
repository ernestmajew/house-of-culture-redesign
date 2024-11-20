import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsDisplayComponent } from './news-display.component';

describe('NewsDisplayComponent', () => {
  let component: NewsDisplayComponent;
  let fixture: ComponentFixture<NewsDisplayComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewsDisplayComponent]
    });
    fixture = TestBed.createComponent(NewsDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
