import { EmployeeDTO } from "../employee.dto";
import { VehicleDTO } from "../vehicle/vehicle.dto";
import { CoordinatesDTO } from "./coordinates.dto";

export interface CarpoolDTO {

    id?: number;
    vehicleId?: number;
    creationTime?: string;
    estimatedArrivalTime?: string;
    estimatedDepartureTime: string;
    estimatedDuration?: number;
    remainingSeats?: number;
    occupiedSeats?: number;
    initialAvailableSeats?:number;
    estimatedLength?: number;
    startCoordinates: CoordinatesDTO;
    endCoordinates: CoordinatesDTO;
    isCanceled?: boolean;
    vehicle?: VehicleDTO;
    participants?: EmployeeDTO[];
    organizer?:EmployeeDTO;
    
}
