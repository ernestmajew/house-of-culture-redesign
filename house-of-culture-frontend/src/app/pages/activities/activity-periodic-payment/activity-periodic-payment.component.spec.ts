import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityPeriodicPaymentComponent } from './activity-periodic-payment.component';

describe('ActivityPeriodicPaymentComponent', () => {
  let component: ActivityPeriodicPaymentComponent;
  let fixture: ComponentFixture<ActivityPeriodicPaymentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActivityPeriodicPaymentComponent]
    });
    fixture = TestBed.createComponent(ActivityPeriodicPaymentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
