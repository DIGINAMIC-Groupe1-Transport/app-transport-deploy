// vehicle-reservation.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { VehiclesService } from '../../shared/services/vehicles.service';
// FIX: Check the actual filename and adjust the import accordingly.
// For example, if the file is named 'vehicle-reservation.service.ts' (singular), use:

// Or, if the correct file is 'vehicle-reservations.service.ts', ensure it exists at the specified path.

import { MatDialog } from '@angular/material/dialog';
import { EditReservationDialogComponent } from './edit-reservation-dialog.component';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';
import { VehicleReservationDto } from 'app/shared/models/vehicle/employee.dto';
import { VehicleReservationsService } from 'app/shared/services/vehicle-reservation.service';
import { VehicleStatus } from 'app/shared/enums/vehicle/vehicle-status.enum';

@Component({
  selector: 'app-vehicle-reservation',
  templateUrl: './vehicle-reservation.component.html',
  styleUrls: ['./vehicle-reservation.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
})
export class VehicleReservationComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  reservationForm: FormGroup;
  vehicles: VehicleDTO[] = [];
  selectedVehicle: VehicleDTO | null = null;
  currentVehicleIndex: number = 0;

  loading: boolean = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  reservations: VehicleReservationDto[] = [];
  activeTab: 'new' | 'current' | 'history' = 'new';

  editMode: boolean = false;
  reservationToEdit: VehicleReservationDto | null = null;

  constructor(
    private fb: FormBuilder,
    private vehiclesService: VehiclesService,
    private reservationsService: VehicleReservationsService,
    private dialog: MatDialog
  ) {
    this.reservationForm = this.fb.group({
      vehicleId: ['', Validators.required],
      startDateTime: ['', Validators.required],
      endDateTime: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.vehiclesService
      .getAllVehicles()
      .then((vehicles) => {
        this.vehicles = vehicles;
      })
      .catch((error) => {
        console.error('Erreur lors de la récupération des véhicules:', error);
      });
    this.loadReservations();
    this.subscribeToServices();
    // Filtrage automatique des véhicules dès que les deux dates sont valides
    this.reservationForm.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        if (value.startDateTime && value.endDateTime && this.areDatesValid()) {
          this.searchAvailableVehicles();
        } else {
          this.vehicles = [];
          this.selectedVehicle = null;
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private subscribeToServices(): void {
    this.reservationsService.loading$
      .pipe(takeUntil(this.destroy$))
      .subscribe((loading) => (this.loading = loading));

    this.reservationsService.reservations$
      .pipe(takeUntil(this.destroy$))
      .subscribe((reservations) => (this.reservations = reservations));

    this.vehiclesService
      .getAllVehicles()
      .then((vehicles) => {
        this.vehicles = vehicles;
        console.log('DEBUG véhicules récupérés dans le composant:', vehicles);
        // this.vehicles = vehicles; // si tu veux les stocker
      })
      .catch((error) => {
        console.error('Erreur lors de la récupération des véhicules:', error);
      });
  }

  private loadReservations(): void {
    this.reservationsService.loadReservations();
  }

  nextVehicle(): void {
    if (this.vehicles.length > 0) {
      this.currentVehicleIndex =
        (this.currentVehicleIndex + 1) % this.vehicles.length;
      this.selectedVehicle = this.vehicles[this.currentVehicleIndex];
    }
  }

  prevVehicle(): void {
    if (this.vehicles.length > 0) {
      this.currentVehicleIndex =
        (this.currentVehicleIndex - 1 + this.vehicles.length) %
        this.vehicles.length;
      this.selectedVehicle = this.vehicles[this.currentVehicleIndex];
    }
  }

  onSelectVehicle(vehicle: VehicleDTO): void {
    this.selectedVehicle = vehicle;
    this.currentVehicleIndex = this.vehicles.findIndex(
      (v) => v.id === vehicle.id
    );
  }

  setActiveTab(tab: 'new' | 'current' | 'history'): void {
    this.activeTab = tab;
  }

  async onSubmit(): Promise<void> {
    if (!this.reservationForm.valid) {
      this.showError('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const formValue = this.reservationForm.value;
    const toBackendDateFormat = (dateStr: string) =>
      dateStr.length === 16 ? dateStr + ':00' : dateStr;

    if (this.editMode && this.reservationToEdit !== null) {
      // Mode édition
      const updated = {
        id: this.reservationToEdit.id,
        vehicleId: Number(formValue.vehicleId),
        dateDebut: toBackendDateFormat(formValue.startDateTime),
        dateFin: toBackendDateFormat(formValue.endDateTime),
        status: 'ACTIVE',
      };
      try {
        await this.reservationsService.updateReservation(updated as any);
        this.showSuccess('Réservation modifiée avec succès !');
        this.cancelEdit();
        this.loadReservations();
      } catch (error) {
        this.showError('Erreur lors de la modification');
      }
    } else {
      // Mode création
      const reservation = {
        vehicleId: Number(formValue.vehicleId),
        dateDebut: toBackendDateFormat(formValue.startDateTime),
        dateFin: toBackendDateFormat(formValue.endDateTime),
        status: 'ACTIVE',
      };
      try {
        await this.reservationsService.addReservation(reservation);
        this.showSuccess('Réservation effectuée avec succès !');
        this.reservationForm.reset();
        this.loadReservations();
      } catch (error) {
        this.showError('Erreur lors de la réservation du véhicule');
      }
    }
  }

  async cancelReservation(reservationId: number | undefined): Promise<void> {
    try {
      await this.reservationsService.deleteReservation(reservationId ?? 0);
      this.showSuccess('Réservation annulée avec succès');
    } catch (error) {
      console.error('Error canceling reservation:', error);
      this.showError("Erreur lors de l'annulation de la réservation");
    }
  }

  private resetForm(): void {
    this.reservationForm.reset();
   // this.resetVehicleSelection();
  }

  private showSuccess(message: string): void {
    this.successMessage = message;
    this.errorMessage = null;
    setTimeout(() => (this.successMessage = null), 3000);
  }

  private showError(message: string): void {
    this.errorMessage = message;
    this.successMessage = null;
    setTimeout(() => (this.errorMessage = null), 3000);
  }

  getMotorIconClass(motorisation: string): string {
    switch (motorisation?.toLowerCase()) {
      case 'électrique':
        return 'motor-icon electric';
      case 'hybride':
        return 'motor-icon hybrid';
      default:
        return 'motor-icon fuel';
    }
  }

  getCO2Class(co2: number): string {
    if (co2 === 0) return 'co2-excellent';
    if (co2 < 100) return 'co2-good';
    return 'co2-average';
  }

  formatDateTime(dateTime: string): string {
    return new Date(dateTime).toLocaleDateString('fr-FR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatForInput(date: any): string {
    const d = new Date(date);
    const pad = (n: number) => (n < 10 ? '0' + n : n);
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(
      d.getDate()
    )}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  get currentReservations(): VehicleReservationDto[] {
    return this.reservations.filter(
      (r) => new Date(r.endDateTime) > new Date()
    );
  }

  get pastReservations(): VehicleReservationDto[] {
    return this.reservations.filter(
      (r) => new Date(r.endDateTime) <= new Date()
    );
  }

  get progressPercentage(): number {
    if (this.vehicles.length === 0) return 0;
    return ((this.currentVehicleIndex + 1) / this.vehicles.length) * 100;
  }

  openEditForm(reservation: VehicleReservationDto) {
    this.editMode = true;
    this.reservationToEdit = reservation;
    this.reservationForm.patchValue({
      vehicleId: reservation.vehicleId,
      startDateTime: this.formatForInput(reservation.startDateTime),
      endDateTime: this.formatForInput(reservation.endDateTime),
    });
  }

  cancelEdit() {
    this.editMode = false;
    this.reservationToEdit = null;
    this.reservationForm.reset();
  }

  async onSubmitEdit() {
    if (!this.reservationForm.valid || !this.reservationToEdit) return;
    const formValue = this.reservationForm.value;
    const toBackendDateFormat = (dateStr: string) =>
      dateStr.length === 16 ? dateStr + ':00' : dateStr;
    const updated = {
      id: this.reservationToEdit.id ?? 0,
      vehicleId: Number(formValue.vehicleId),
      dateDebut: toBackendDateFormat(formValue.startDateTime),
      dateFin: toBackendDateFormat(formValue.endDateTime),
      status: 'ACTIVE',
    };
    try {
      await this.reservationsService.updateReservation(updated as any);
      this.showSuccess('Réservation modifiée avec succès !');
      this.cancelEdit();
      this.loadReservations();
    } catch (error) {
      this.showError('Erreur lors de la modification');
    }
  }

  openEditDialog(reservation: VehicleReservationDto) {
    const dialogRef = this.dialog.open(EditReservationDialogComponent, {
      data: {
        reservation,
        vehicles: this.vehicles,
      },
    });

    dialogRef.afterClosed().subscribe(async (result) => {
      if (result) {
        // Adapter le mapping pour l'API si besoin
        const toBackendDateFormat = (dateStr: string) =>
          dateStr.length === 16 ? dateStr + ':00' : dateStr;
        const updated = {
          id: result.id,
          vehicleId: Number(result.vehicleId),
          dateDebut: toBackendDateFormat(result.startDateTime),
          dateFin: toBackendDateFormat(result.endDateTime),
          status: result.status || 'ACTIVE',
        };
        try {
          await this.reservationsService.updateReservation(updated as any);
          this.showSuccess('Réservation modifiée avec succès !');
          this.loadReservations();
        } catch (error) {
          this.showError('Erreur lors de la modification');
        }
      }
    });
  }

  /**
   * Vérifie que la date de fin est postérieure à la date de début
   */
  areDatesValid(): boolean {
    const start = this.reservationForm.get('startDateTime')?.value;
    const end = this.reservationForm.get('endDateTime')?.value;
    if (!start || !end) return false;
    return new Date(start) < new Date(end);
  }

  /**
   * Filtre les véhicules disponibles selon la période et le statut
   */
  async searchAvailableVehicles(): Promise<void> {
    const start = this.reservationForm.get('startDateTime')?.value;
    const end = this.reservationForm.get('endDateTime')?.value;
    if (!start || !end || !this.areDatesValid()) {
      this.showError('Veuillez saisir une période valide.');
      return;
    }
    this.loading = true;
    try {
      const allVehicles = await this.vehiclesService.getAllVehicles();
      // On suppose que les réservations sont déjà chargées dans this.reservations
      const filtered = allVehicles.filter(v => {
        // Statut "disponible" uniquement
        if (v.vehicleStatus !== VehicleStatus.AVAILABLE) return false;
        // Vérifier qu'il n'y a pas de conflit de réservation
        const hasConflict = this.reservations.some(r =>
          r.vehicle?.id === v.id &&
          ((new Date(start) < new Date(r.endDateTime)) && (new Date(end) > new Date(r.startDateTime)))
        );
        return !hasConflict;
      });
      this.vehicles = filtered;
      this.selectedVehicle = filtered.length > 0 ? filtered[0] : null;
      this.currentVehicleIndex = 0;
      if (filtered.length === 0) {
        this.showError('Aucun véhicule disponible pour la période sélectionnée.');
      }
    } catch (error) {
      this.showError('Erreur lors de la recherche des véhicules.');
    } finally {
      this.loading = false;
    }
  }
}
