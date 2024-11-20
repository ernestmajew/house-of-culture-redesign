import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleEventActionDialogComponent } from './single-event-action-dialog.component';

describe('SingleEventActionDialogComponent', () => {
  let component: SingleEventActionDialogComponent;
  let fixture: ComponentFixture<SingleEventActionDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SingleEventActionDialogComponent]
    });
    fixture = TestBed.createComponent(SingleEventActionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
