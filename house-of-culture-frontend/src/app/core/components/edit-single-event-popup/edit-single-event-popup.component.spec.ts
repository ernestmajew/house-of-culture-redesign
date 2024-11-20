import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSingleEventPopupComponent } from './edit-single-event-popup.component';

describe('EditSingleEventPopupComponent', () => {
  let component: EditSingleEventPopupComponent;
  let fixture: ComponentFixture<EditSingleEventPopupComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditSingleEventPopupComponent]
    });
    fixture = TestBed.createComponent(EditSingleEventPopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
