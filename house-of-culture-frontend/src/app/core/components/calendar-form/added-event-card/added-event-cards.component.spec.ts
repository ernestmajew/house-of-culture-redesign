import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddedEventCardsComponent } from './added-event-cards.component';

describe('AddedEventCardComponent', () => {
  let component: AddedEventCardsComponent;
  let fixture: ComponentFixture<AddedEventCardsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddedEventCardsComponent]
    });
    fixture = TestBed.createComponent(AddedEventCardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
