import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditActivitiesComponent } from './edit-activities.component';

describe('EditActivitiesComponent', () => {
  let component: EditActivitiesComponent;
  let fixture: ComponentFixture<EditActivitiesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditActivitiesComponent]
    });
    fixture = TestBed.createComponent(EditActivitiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
