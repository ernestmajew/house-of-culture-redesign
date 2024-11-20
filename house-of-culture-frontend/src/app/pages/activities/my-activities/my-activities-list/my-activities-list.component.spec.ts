import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyActivitiesListComponent } from './my-activities-list.component';

describe('MyActivitiesListComponent', () => {
  let component: MyActivitiesListComponent;
  let fixture: ComponentFixture<MyActivitiesListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MyActivitiesListComponent]
    });
    fixture = TestBed.createComponent(MyActivitiesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
