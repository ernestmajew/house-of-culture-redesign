import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityOfferCardComponent } from './activity-offer-card.component';

describe('ActivivtyOfferCardComponent', () => {
  let component: ActivityOfferCardComponent;
  let fixture: ComponentFixture<ActivityOfferCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ActivityOfferCardComponent]
    });
    fixture = TestBed.createComponent(ActivityOfferCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
