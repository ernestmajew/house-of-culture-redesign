import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewNewsFormComponent } from './new-news-form.component';

describe('NewNewsFormComponent', () => {
  let component: NewNewsFormComponent;
  let fixture: ComponentFixture<NewNewsFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewNewsFormComponent]
    });
    fixture = TestBed.createComponent(NewNewsFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
