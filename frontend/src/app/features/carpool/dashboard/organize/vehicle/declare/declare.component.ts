import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VehicleCategory } from 'app/shared/enums/vehicle/vehicle-category.enum';
import { VehicleMotor } from 'app/shared/enums/vehicle/vehicle-motor.enum';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';
import { VehiclesService } from 'app/shared/services/vehicles.service';

@Component({
  selector: 'app-declare',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './declare.component.html',
  styleUrl: './declare.component.css'
})
export class DeclareComponent {

  vehicleMotors = Object.entries(VehicleMotor);
  vehicleCategorys = Object.entries(VehicleCategory)

  selectedVehicleMotor: string = '';
  selectedVehicleCategory: string = '';

  vehicleRegistration: string = '';
  vehicleModel: string = '';
  vehicleBrand: string = '';
  vehicleSeats: number = 0;
  vehicleCO2?: number;

  constructor(
    private vehiclesService: VehiclesService
  ) { }

  // const [selectedMotor, setSelectedMotor] = useState<VehicleMotor | undefined>();

  onSubmit(): void {


    // if (!this.vehicleRegistration.trim() || this.vehicleBrand.trim() ||  this.vehicleModel.trim() || !this.selectedVehicleCategory.trim() || this.selectedVehicleMotor.trim()) {
    //   return;
    // }

    const vehicle: VehicleDTO = {
      registration: this.vehicleRegistration,
      brand: this.vehicleBrand,
      model: this.vehicleModel,
      seats: this.vehicleSeats,
      co2PerKm: this.vehicleCO2,
      vehicleCategory: this.selectedVehicleCategory as VehicleCategory,
      vehicleMotor: this.selectedVehicleMotor as VehicleMotor
    }

    this.vehiclesService.createPersonalVehicle(vehicle).subscribe({
      next: (response) => {
        if (response) {
          console.log('Covoiturage créé avec succès:', response);
        } else {
          console.error('Échec de la création du covoiturage');
        }
      },
      error: (error) => {
        console.error('Erreur lors de la création:', error);
      }
    });

  }

}
