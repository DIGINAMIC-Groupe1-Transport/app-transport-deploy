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
import { VehicleStatus } from 'app/shared/enums/vehicle/vehicle-status.enum';
import { VehicleCategory } from 'app/shared/enums/vehicle/vehicle-category.enum';
import { VehicleMotor } from 'app/shared/enums/vehicle/vehicle-motor.enum';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';


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
  templateUrl: './vehicle-dialog.component.html',
  styleUrls: ['./vehicle-dialog.component.css'],
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
