import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageConnectionDialogComponent } from './page-connection-dialog.component';

describe('PageConnectionDialogComponent', () => {
  let component: PageConnectionDialogComponent;
  let fixture: ComponentFixture<PageConnectionDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PageConnectionDialogComponent]
    });
    fixture = TestBed.createComponent(PageConnectionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
