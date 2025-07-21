import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { CommonModule } from '@angular/common';
import { VehicleDTO } from 'app/shared/models/vehicle/vehicle.dto';
import { VehiclesService } from 'app/shared/services/vehicles.service';

@Component({
  selector: 'app-vehicle-add',
  templateUrl: './vehicle-add.component.html',
  styleUrls: ['./vehicle-add.component.css'],
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule]
})
export class VehicleAddComponent implements OnInit {
  vehicleForm: FormGroup;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private vehiclesService: VehiclesService
  ) {
    this.vehicleForm = this.fb.group({
      immatriculation: ['', Validators.required],
      marque: ['', Validators.required],
      modele: ['', Validators.required],
      categorie: ['', Validators.required],
      photo: ['', [Validators.required]],
      places: [1, [Validators.required, Validators.min(1)]],
      motorisation: ['', Validators.required],
      co2: [0, [Validators.required, Validators.min(0)]],
      statut: ['en_service', Validators.required]
    });
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.vehicleForm.valid) {
      const formValue = this.vehicleForm.value as VehicleDTO;
      this.vehiclesService.addVehicle(formValue)
        .then(() => {
          this.successMessage = 'Véhicule ajouté avec succès !';
          this.errorMessage = null;
          this.vehicleForm.reset({ statut: 'en_service', places: 1, co2: 0 });
        })
        .catch((err: any) => {
          this.successMessage = null;
          this.errorMessage = "Erreur lors de l'ajout du véhicule.";
        });
    }
  }
} 