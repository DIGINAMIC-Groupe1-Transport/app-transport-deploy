import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

export interface EditReservationData {
  reservation: any;
  vehicles: any[];
}

@Component({
  selector: 'app-edit-reservation-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  template: `
    <div class="edit-dialog-backdrop" style="display: flex; align-items: center; justify-content: center; min-height: 100vh;">
      <div class="edit-dialog" style="background: #fff; border-radius: 16px; padding: 2rem; min-width: 320px; max-width: 400px; width: 100%; box-shadow: 0 8px 32px rgba(0,0,0,0.15);">
        <h2 mat-dialog-title class="dialog-title">
          <i class="icon-edit"></i>
          Modifier la réservation
        </h2>
        <mat-dialog-content class="dialog-content">
          <form [formGroup]="editForm" class="edit-form">
            <div class="form-group">
              <label for="vehicleId">Véhicule *</label>
              <select id="vehicleId" formControlName="vehicleId" class="form-input">
                <option value="" disabled>Choisissez un véhicule</option>
                <option *ngFor="let v of data.vehicles" [value]="v.id">
                  {{ v.brand }} {{ v.model }} ({{ v.registration }})
                </option>
              </select>
            </div>
            <div class="form-group">
              <label for="startDateTime">Date et heure de début *</label>
              <input type="datetime-local" id="startDateTime" formControlName="startDateTime" class="form-input">
            </div>
            <div class="form-group">
              <label for="endDateTime">Date et heure de fin *</label>
              <input type="datetime-local" id="endDateTime" formControlName="endDateTime" class="form-input">
            </div>
            <div class="vehicle-preview" *ngIf="selectedVehicle">
              <div class="vehicle-preview-header">
                <h3>Aperçu du véhicule</h3>
              </div>
              <div class="vehicle-info-card">
                <img [src]="selectedVehicle.urlImage" [alt]="selectedVehicle.brand + ' ' + selectedVehicle.model" class="vehicle-preview-image">
                <div class="vehicle-details">
                  <h4>{{ selectedVehicle.brand }} {{ selectedVehicle.model }}</h4>
                  <p>{{ selectedVehicle.registration }}</p>
                  <div class="vehicle-specs">
                    <span class="spec-item">
                      <i class="icon-users"></i>
                      {{ selectedVehicle.seats }} places
                    </span>
                    <span class="spec-item">
                      <i class="icon-settings"></i>
                      {{ selectedVehicle.vehicleMotor }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </form>
        </mat-dialog-content>
        <mat-dialog-actions class="dialog-actions" style="display: flex; justify-content: flex-end; gap: 1rem; margin-top: 1.5rem;">
          <button type="button" class="cancel-button" (click)="onCancel()">Annuler</button>
          <button type="button" class="save-button" [disabled]="!editForm.valid" (click)="onSave()">Sauvegarder</button>
        </mat-dialog-actions>
      </div>
    </div>
  `
})
export class EditReservationDialogComponent implements OnInit {
  editForm: FormGroup;
  selectedVehicle: any = null;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EditReservationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditReservationData
  ) {
    this.editForm = this.fb.group({
      vehicleId: ['', [Validators.required]],
      startDateTime: ['', [Validators.required]],
      endDateTime: ['', [Validators.required]]
    });
  }

  ngOnInit() {
    // Pré-remplir le formulaire avec les données de la réservation
    this.populateForm();
    
    // Écouter les changements du véhicule sélectionné
    this.editForm.get('vehicleId')?.valueChanges.subscribe(vehicleId => {
      this.selectedVehicle = this.data.vehicles.find(v => v.id === vehicleId);
    });
  }

  private populateForm() {
    const reservation = this.data.reservation;
    
    // Convertir les dates en format datetime-local
    const startDateTime = this.formatDateTimeLocal(reservation.startDateTime);
    const endDateTime = this.formatDateTimeLocal(reservation.endDateTime);
    
    this.editForm.patchValue({
      vehicleId: reservation.vehicle?.id || '',
      startDateTime: startDateTime,
      endDateTime: endDateTime
    });
    
    // Définir le véhicule sélectionné initial
    this.selectedVehicle = reservation.vehicle;
  }

  private formatDateTimeLocal(dateTime: string | Date): string {
    const date = new Date(dateTime);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  onSave() {
    if (this.editForm.valid) {
      const formValue = this.editForm.value;
      const updatedReservation = {
        ...this.data.reservation,
        vehicleId: formValue.vehicleId,
        startDateTime: new Date(formValue.startDateTime),
        endDateTime: new Date(formValue.endDateTime),
        vehicle: this.selectedVehicle
      };
      
      this.dialogRef.close(updatedReservation);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
} 