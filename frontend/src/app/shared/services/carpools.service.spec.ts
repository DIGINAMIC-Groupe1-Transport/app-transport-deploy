import { TestBed } from '@angular/core/testing';

import { CarpoolsService } from './carpools.service';

describe('CarpoolsService', () => {
  let service: CarpoolsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CarpoolsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
