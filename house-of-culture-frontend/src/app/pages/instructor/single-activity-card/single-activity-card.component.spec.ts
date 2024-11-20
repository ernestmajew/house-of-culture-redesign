import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleActivityCardComponent } from './single-activity-card.component';

describe('SingleActivityCardComponent', () => {
  let component: SingleActivityCardComponent;
  let fixture: ComponentFixture<SingleActivityCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SingleActivityCardComponent]
    });
    fixture = TestBed.createComponent(SingleActivityCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
