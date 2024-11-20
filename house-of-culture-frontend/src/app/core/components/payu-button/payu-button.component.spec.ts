import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PayuButtonComponent} from './payu-button.component';

describe('PayuButtonComponent', () => {
  let component: PayuButtonComponent;
  let fixture: ComponentFixture<PayuButtonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PayuButtonComponent]
    });
    fixture = TestBed.createComponent(PayuButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
