import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitiesCalendarComponent } from './activities-calendar.component';

describe('MyActivitiesCalendarComponent', () => {
  let component: ActivitiesCalendarComponent;
  let fixture: ComponentFixture<ActivitiesCalendarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ActivitiesCalendarComponent]
    });
    fixture = TestBed.createComponent(ActivitiesCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
