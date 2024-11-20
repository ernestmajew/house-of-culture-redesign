import { TestBed } from '@angular/core/testing';

import { SingleEventService } from './single-event.service';

describe('SingleEventService', () => {
  let service: SingleEventService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SingleEventService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
