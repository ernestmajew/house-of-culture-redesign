import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnrollOccurrencesSelectorComponent } from './enroll-occurrences-selector.component';

describe('EnrollOccurrencesSelectorComponent', () => {
  let component: EnrollOccurrencesSelectorComponent;
  let fixture: ComponentFixture<EnrollOccurrencesSelectorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EnrollOccurrencesSelectorComponent]
    });
    fixture = TestBed.createComponent(EnrollOccurrencesSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
