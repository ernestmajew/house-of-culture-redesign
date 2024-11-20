import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignOutEventsPopupComponent } from './sign-out-events-popup.component';

describe('SignOutEventsPopupComponent', () => {
  let component: SignOutEventsPopupComponent;
  let fixture: ComponentFixture<SignOutEventsPopupComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SignOutEventsPopupComponent]
    });
    fixture = TestBed.createComponent(SignOutEventsPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
