import { VehicleCategory } from '../../enums/vehicle/vehicle-category.enum';
import { VehicleMotor } from '../../enums/vehicle/vehicle-motor.enum';
import { VehicleStatus } from '../../enums/vehicle/vehicle-status.enum';

export interface VehicleDTO {
  id?: number;
  registration: string;
  brand?: string;
  model: string;
  seats: number;
  isCompany?: boolean;
  co2PerKm?: number;
  vehicleStatus?: VehicleStatus;
  vehicleCategory?: VehicleCategory;
  vehicleMotor?: VehicleMotor;
  urlImage?: string;
}
