import { Component, Inject, OnInit, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';
import { VehicleStatus } from 'app/shared/enums/vehicle/vehicle-status.enum';
import { VehicleCategory } from 'app/shared/enums/vehicle/vehicle-category.enum';
import { VehicleMotor } from 'app/shared/enums/vehicle/vehicle-motor.enum';
@Component({
  selector: 'app-vehicle-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Modifier' : 'Ajouter' }} un véhicule</h2>
    <form [formGroup]="vehicleForm" (ngSubmit)="onSubmit()">
      <mat-form-field appearance="outline">
        <mat-label>Immatriculation</mat-label>
        <input matInput formControlName="registration" required />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Marque</mat-label>
        <input matInput formControlName="brand" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Modèle</mat-label>
        <input matInput formControlName="model" required />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Nombre de places</mat-label>
        <input
          matInput
          type="number"
          formControlName="seats"
          required
          min="1"
        />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Statut</mat-label>
        <mat-select formControlName="vehicleStatus">
          <mat-option *ngFor="let s of statusOptions" [value]="s.value">{{
            s.label
          }}</mat-option>
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Catégorie</mat-label>
        <mat-select formControlName="vehicleCategory">
          <mat-option *ngFor="let c of categoryOptions" [value]="c.value">{{
            c.label
          }}</mat-option>
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Motorisation</mat-label>
        <mat-select formControlName="vehicleMotor">
          <mat-option *ngFor="let m of motorOptions" [value]="m.value">{{
            m.label
          }}</mat-option>
        </mat-select>
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>CO2/km</mat-label>
        <input matInput type="number" formControlName="co2PerKm" min="0" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Image (URL)</mat-label>
        <input matInput formControlName="urlImage" />
      </mat-form-field>
      <div
        style="display: flex; justify-content: flex-end; gap: 8px; margin-top: 16px;"
      >
        <button mat-button type="button" (click)="onCancel()">Annuler</button>
        <button
          mat-raised-button
          color="primary"
          type="submit"
          [disabled]="vehicleForm.invalid"
        >
          {{ data ? 'Modifier' : 'Ajouter' }}
        </button>
      </div>
    </form>
  `,
})
export class VehicleDialogComponent implements OnInit {
  vehicleForm: FormGroup;
  statusOptions = [
    { value: VehicleStatus.AVAILABLE, label: 'Disponible' },
    { value: VehicleStatus.UNAVAILABLE, label: 'Indisponible' },
    { value: VehicleStatus.MAINTENANCE, label: 'Maintenance' },
  ];
  categoryOptions = [
    { value: VehicleCategory.TOUT_TERRAIN, label: 'Tout terrain' },
    { value: VehicleCategory.BERLINE_TAILLE_L, label: 'Berline taille L' },
    { value: VehicleCategory.BERLINE_TAILLE_M, label: 'Berline taille M' },
    { value: VehicleCategory.SUV, label: 'SUV' },
    { value: VehicleCategory.MINI_CITADINE, label: 'Mini citadine' },
    { value: VehicleCategory.MICRO_URBAINE, label: 'Micro urbaine' },
    { value: VehicleCategory.PICKUP, label: 'Pickup' },
    {
      value: VehicleCategory.CITADINE_POLYVALENTE,
      label: 'Citadine polyvalente',
    },
    { value: VehicleCategory.COMPACTE, label: 'Compacte' },
    { value: VehicleCategory.BERLINE_TAILLE_S, label: 'Berline taille S' },
  ];
  motorOptions = [
    { value: VehicleMotor.GASOLINE, label: 'Essence' },
    { value: VehicleMotor.DIESEL, label: 'Diesel' },
    { value: VehicleMotor.HYBRID, label: 'Hybride' },
    { value: VehicleMotor.ELECTRIC, label: 'Électrique' },
  ];

  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<VehicleDialogComponent>);
  public data: VehicleDTO | null;

  constructor(@Inject(MAT_DIALOG_DATA) data: VehicleDTO | null) {
    this.data = data;
    this.vehicleForm = this.fb.group({
      registration: [data?.registration || '', Validators.required],
      brand: [data?.brand || ''],
      model: [data?.model || '', Validators.required],
      seats: [data?.seats || 1, [Validators.required, Validators.min(1)]],
      vehicleStatus: [data?.vehicleStatus || VehicleStatus.AVAILABLE],
      vehicleCategory: [data?.vehicleCategory || VehicleCategory.TOUT_TERRAIN],
      vehicleMotor: [data?.vehicleMotor || VehicleMotor.GASOLINE],
      co2PerKm: [data?.co2PerKm || 0, [Validators.min(0)]],
      // urlImage: [data?.urlImage || '']
    });
  }

  ngOnInit() {}

  onSubmit() {
    if (this.vehicleForm.valid) {
      this.dialogRef.close(this.vehicleForm.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
