import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AuthButtonsComponent } from './auth-buttons.component';

describe('AuthButtonsComponent', () => {
  let component: AuthButtonsComponent;
  let fixture: ComponentFixture<AuthButtonsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AuthButtonsComponent]
    });
    fixture = TestBed.createComponent(AuthButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
