import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityPaymentsComponent } from './activity-payments.component';

describe('ActivityPaymentsComponent', () => {
  let component: ActivityPaymentsComponent;
  let fixture: ComponentFixture<ActivityPaymentsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActivityPaymentsComponent]
    });
    fixture = TestBed.createComponent(ActivityPaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
