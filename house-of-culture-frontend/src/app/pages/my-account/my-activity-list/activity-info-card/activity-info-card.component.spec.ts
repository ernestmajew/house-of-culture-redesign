import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityInfoCardComponent } from './activity-info-card.component';

describe('ActivityInfoCardComponent', () => {
  let component: ActivityInfoCardComponent;
  let fixture: ComponentFixture<ActivityInfoCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActivityInfoCardComponent]
    });
    fixture = TestBed.createComponent(ActivityInfoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
