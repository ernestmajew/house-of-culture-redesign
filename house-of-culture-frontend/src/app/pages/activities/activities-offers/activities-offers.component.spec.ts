import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitiesOffersComponent } from './activities-offers.component';

describe('ActivitiesOffersComponent', () => {
  let component: ActivitiesOffersComponent;
  let fixture: ComponentFixture<ActivitiesOffersComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActivitiesOffersComponent]
    });
    fixture = TestBed.createComponent(ActivitiesOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
