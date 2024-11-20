import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitiesRowComponent } from './activities-row.component';

describe('ActivitiesRowComponent', () => {
  let component: ActivitiesRowComponent;
  let fixture: ComponentFixture<ActivitiesRowComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ActivitiesRowComponent]
    });
    fixture = TestBed.createComponent(ActivitiesRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
