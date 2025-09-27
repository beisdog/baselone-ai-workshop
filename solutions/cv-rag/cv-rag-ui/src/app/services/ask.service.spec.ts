import { TestBed } from '@angular/core/testing';

import { AskService } from './ask.service';

describe('AskService', () => {
  let service: AskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AskService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
