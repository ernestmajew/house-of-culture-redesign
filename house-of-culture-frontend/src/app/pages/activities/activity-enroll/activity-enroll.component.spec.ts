import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityEnrollComponent } from './activity-enroll.component';

describe('ActivityEnrollComponent', () => {
  let component: ActivityEnrollComponent;
  let fixture: ComponentFixture<ActivityEnrollComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActivityEnrollComponent]
    });
    fixture = TestBed.createComponent(ActivityEnrollComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
