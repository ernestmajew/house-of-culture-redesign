import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnrolledUserListComponent } from './enrolled-user-list.component';

describe('EnrolledUserListComponent', () => {
  let component: EnrolledUserListComponent;
  let fixture: ComponentFixture<EnrolledUserListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnrolledUserListComponent]
    });
    fixture = TestBed.createComponent(EnrolledUserListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
