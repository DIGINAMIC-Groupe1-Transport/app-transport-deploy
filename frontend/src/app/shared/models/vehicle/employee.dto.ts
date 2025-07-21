export interface VehicleReservationDto {
  id?: number;
  vehicleId: number;
  userId: number;
  startDateTime: Date;
  endDateTime: Date;
  status?: string;
  vehicle?: {
    id: number;
    brand: string;
    model: string;
    registration: string;
    urlImage?: string;
    vehicleCategory?: string;
    seats?: number;
    vehicleMotor?: string;
    co2PerKm?: number;
  };
}
