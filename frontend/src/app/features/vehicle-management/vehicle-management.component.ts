import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { VehicleDTO } from '../../shared/models/vehicle/vehicle.dto';
import { VehiclesService } from '../../shared/services/vehicles.service';
import { VehicleStatus } from '../../shared/enums/vehicle/vehicle-status.enum';
import { VehicleCategory } from '../../shared/enums/vehicle/vehicle-category.enum';
import { VehicleMotor } from '../../shared/enums/vehicle/vehicle-motor.enum';

@Component({
  selector: 'app-vehicle-management',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    MatToolbarModule,
    MatSnackBarModule,
  ],
  templateUrl: './vehicle-management.component.html',
  styleUrls: ['./vehicle-management.component.css'],
})
export class VehicleManagementComponent implements OnInit {
  vehicles: VehicleDTO[] = [];
  displayedColumns: string[] = [
    'registration',
    'brand',
    'model',
    'seats',
    'vehicleStatus',
    'vehicleCategory',
    'vehicleMotor',
    'co2PerKm',
    'actions',
  ];

  private vehiclesService = inject(VehiclesService);
  private dialog = inject(MatDialog);
  private snackBar = inject(MatSnackBar);

  ngOnInit() {
    this.loadVehicles();
  }

  async loadVehicles() {
    this.vehicles = await this.vehiclesService.getAllVehicles();
  }

  openVehicleDialog(vehicle?: VehicleDTO) {
    import('./vehicle-dialog.component').then(({ VehicleDialogComponent }) => {
      const dialogRef = this.dialog.open(VehicleDialogComponent, {
        width: '500px',
        data: vehicle,
      });
      dialogRef.afterClosed().subscribe(async (result) => {
        if (result) {
          if (vehicle && vehicle.id) {
            await this.vehiclesService.updateVehicle(vehicle.id, result);
            this.snackBar.open('Véhicule modifié avec succès', 'Fermer', {
              duration: 3000,
            });
          } else {
            await this.vehiclesService.addVehicle(result);
            this.snackBar.open('Véhicule ajouté avec succès', 'Fermer', {
              duration: 3000,
            });
          }
          await this.loadVehicles();
        }
      });
    });
  }

  async deleteVehicle(id: number) {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce véhicule ?')) {
      await this.vehiclesService.deleteVehicle(id);
      this.snackBar.open('Véhicule supprimé avec succès', 'Fermer', {
        duration: 3000,
      });
      await this.loadVehicles();
    }
  }

  getStatusLabel(status?: VehicleStatus): string {
    switch (status) {
      case VehicleStatus.AVAILABLE:
        return 'Disponible';
      case VehicleStatus.UNAVAILABLE:
        return 'Indisponible';
      case VehicleStatus.MAINTENANCE:
        return 'Maintenance';
      default:
        return '-';
    }
  }

  getCategoryLabel(category?: VehicleCategory): string {
    switch (category) {
      case VehicleCategory.TOUT_TERRAIN:
        return 'Tout terrain';
      case VehicleCategory.BERLINE_TAILLE_L:
        return 'Berline taille L';
      case VehicleCategory.BERLINE_TAILLE_M:
        return 'Berline taille M';
      case VehicleCategory.SUV:
        return 'SUV';
      case VehicleCategory.MINI_CITADINE:
        return 'Mini citadine';
      case VehicleCategory.MICRO_URBAINE:
        return 'Micro urbaine';
      case VehicleCategory.PICKUP:
        return 'Pickup';
      case VehicleCategory.CITADINE_POLYVALENTE:
        return 'Citadine polyvalente';
      case VehicleCategory.COMPACTE:
        return 'Compacte';
      case VehicleCategory.BERLINE_TAILLE_S:
        return 'Berline taille S';
      default:
        return '-';
    }
  }

  getMotorLabel(motor?: VehicleMotor): string {
    switch (motor) {
      case VehicleMotor.GASOLINE:
        return 'Essence';
      case VehicleMotor.DIESEL:
        return 'Diesel';
      case VehicleMotor.HYBRID:
        return 'Hybride';
      case VehicleMotor.ELECTRIC:
        return 'Électrique';
      default:
        return '-';
    }
  }
}
