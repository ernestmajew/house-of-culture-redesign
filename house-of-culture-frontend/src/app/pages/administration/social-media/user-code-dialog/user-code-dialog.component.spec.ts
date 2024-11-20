import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserCodeDialogComponent } from './user-code-dialog.component';

describe('UserCodeDialogComponent', () => {
  let component: UserCodeDialogComponent;
  let fixture: ComponentFixture<UserCodeDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserCodeDialogComponent]
    });
    fixture = TestBed.createComponent(UserCodeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
