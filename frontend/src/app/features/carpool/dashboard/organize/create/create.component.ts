import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { CoordinatesSearchComponent } from "app/shared/components/coordinates-search/coordinates-search.component";
import { CarpoolDTO } from 'app/shared/models/carpool/carpool.dto';
import { CoordinatesDTO } from 'app/shared/models/carpool/coordinates.dto';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';
import { VehiclesService } from 'app/shared/services/vehicles.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-create',
  standalone: true,
  imports: [FormsModule, CoordinatesSearchComponent, CommonModule],
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent implements OnInit {

  date: string = '';
  time: string = '';

  selectedPersonalVehicleId?: number;
  selectedPersonalVehicle?: VehicleDTO;
  selectedPersonalVehicleAvailableSeatsOptions: number[] = [];

  selectedCarpoolAvailableSeats?: number;

  selectedStartCoordinates!: CoordinatesDTO;
  selectedEndCoordinates!: CoordinatesDTO;

  private destroy$ = new Subject<void>();

  isLoading = true;

  personalVehicles: VehicleDTO[] = [];

  constructor(
    private vehiclesService: VehiclesService,
    private carpoolService: CarpoolsService,
    private cdRef: ChangeDetectorRef

  ) { }

  ngOnInit(): void {
    this.loadData();
  }


  private loadData(): void {
      this.vehiclesService.personalVehicles$.pipe(
        takeUntil(this.destroy$)
      ).subscribe(carpools => {
        this.personalVehicles = carpools;
      });
  }

  onStartCoordinatesSelected(data: CoordinatesDTO) {
    this.selectedStartCoordinates = data;
    this.cdRef.detectChanges();

  }
  onEndCoordinatesSelected(data: CoordinatesDTO) {
    this.selectedEndCoordinates = data;
    this.cdRef.detectChanges();

  }

  onVehicleSelected(): void {
    this.selectedPersonalVehicle = this.personalVehicles.find(v => v.id == this.selectedPersonalVehicleId);
    if (this.selectedPersonalVehicle?.seats) {
      this.selectedPersonalVehicleAvailableSeatsOptions = Array.from({ length: this.selectedPersonalVehicle.seats - 1 }, (_, i) => i + 1);
    } else {
      this.selectedPersonalVehicleAvailableSeatsOptions = [];
    }
  }

  private formatToEstimatedDepartureTime(): string {
    if (!this.date || !this.time) {
      return '';
    }

    const dateTime = new Date(`${this.date}T${this.time}:00`);

    return dateTime.toISOString().slice(0, 19);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSubmit(): void {
    console.log("TEST 1")
    const organizedCarpool: CarpoolDTO = {
      startCoordinates: this.selectedStartCoordinates,
      endCoordinates: this.selectedEndCoordinates,
      vehicleId: this.selectedPersonalVehicleId,
      initialAvailableSeats: this.selectedCarpoolAvailableSeats,
      estimatedDepartureTime: this.formatToEstimatedDepartureTime()
    }
    console.log("TEST 2")


    this.carpoolService.createOrganizedCarpool(organizedCarpool).subscribe({
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
