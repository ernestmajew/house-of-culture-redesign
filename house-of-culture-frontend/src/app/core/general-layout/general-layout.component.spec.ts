import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneralLayoutComponent } from './general-layout.component';

describe('GeneralLayoutComponent', () => {
  let component: GeneralLayoutComponent;
  let fixture: ComponentFixture<GeneralLayoutComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GeneralLayoutComponent]
    });
    fixture = TestBed.createComponent(GeneralLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
