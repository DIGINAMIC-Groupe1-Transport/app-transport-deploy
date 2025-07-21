import { TestBed } from '@angular/core/testing';

import { VehicleReservationsService } from './vehicle-reservations.service';

describe('VehicleReservationsService', () => {
  let service: VehicleReservationsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VehicleReservationsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
