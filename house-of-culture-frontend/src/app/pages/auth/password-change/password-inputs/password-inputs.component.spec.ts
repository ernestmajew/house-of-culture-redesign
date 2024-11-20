import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordInputsComponent } from './password-inputs.component';

describe('PasswordInputsComponent', () => {
  let component: PasswordInputsComponent;
  let fixture: ComponentFixture<PasswordInputsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PasswordInputsComponent]
    });
    fixture = TestBed.createComponent(PasswordInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
