import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewActivitiesFormComponent } from './new-activities-form.component';

describe('CreateNewActivitiesComponent', () => {
  let component: NewActivitiesFormComponent;
  let fixture: ComponentFixture<NewActivitiesFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NewActivitiesFormComponent]
    });
    fixture = TestBed.createComponent(NewActivitiesFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
