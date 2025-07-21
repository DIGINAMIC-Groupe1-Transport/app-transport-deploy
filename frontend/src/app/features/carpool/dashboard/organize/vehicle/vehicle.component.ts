import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';
import { VehiclesService } from 'app/shared/services/vehicles.service';
import { Subject, takeUntil } from 'rxjs';
import { DeclareComponent } from "./declare/declare.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vehicle',
  standalone: true,
  imports: [DeclareComponent, MatTableModule, CommonModule],
  templateUrl: './vehicle.component.html',
  styleUrl: './vehicle.component.css'
})
export class VehicleComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  isLoading = true;

  personalVehicles: VehicleDTO[] = [];

  dataSource = new MatTableDataSource<VehicleDTO>([]);
  displayedColumns: string[] = ['mmatriculation', 'modele', 'marque', 'places', 'co2Km', 'categorie', 'motorisation', 'actions'];


  constructor(
    private vehiclesService: VehiclesService,
    private router: Router,
    private cdRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {

      this.vehiclesService.loadPersonalVehicles().subscribe();

      this.vehiclesService.personalVehicles$.pipe(
        takeUntil(this.destroy$)
      ).subscribe(vehicles => {
        this.personalVehicles = vehicles;
        this.dataSource.data = vehicles;
        this.cdRef.detectChanges();
      });
  }

  onDelete(vehicleId: number): void {
    this.vehiclesService.deletePersonalVehicle(vehicleId).subscribe({
      next: (response) => {
        console.log(response.message);
      },
      error: (err) => {
        console.error('Erreur lors de la suppression', err);
      }
    });
  }


  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
