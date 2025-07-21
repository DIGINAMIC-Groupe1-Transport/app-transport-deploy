import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, firstValueFrom } from 'rxjs';

import { apiUrl } from '../../core/api/api-url-builder';
import { ApiRoutes } from '../../core/api/api-routes';
import { ApiResponse, PaginatedResponse } from '../models/response.model';
import { VehiclesService } from './vehicles.service';
import { VehicleReservationDto } from '../models/vehicle/employee.dto';

@Injectable({
  providedIn: 'root',
})
export class VehicleReservationsService {
  private reservationsSubject = new BehaviorSubject<VehicleReservationDto[]>(
    []
  );
  public reservations$ = this.reservationsSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(
    private http: HttpClient,
    private vehiclesService: VehiclesService
  ) {}

  async loadReservations(): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleReservation.getReservation);
    this.loadingSubject.next(true);

    try {
      const reservations = await firstValueFrom(this.http.get<any[]>(url));

      // Récupérer les informations du véhicule pour chaque réservation
      const reservationsWithVehicles = await Promise.all(
        reservations.map(async (r) => {
          const vehicle = await this.vehiclesService.getVehicleById(
            r.vehicleId
          );
          // vehicle!.urlImage = "https://cdn-icons-png.flaticon.com/512/846/846338.png";
          return {
            ...r,
            startDateTime: new Date(r.dateDebut),
            endDateTime: new Date(r.dateFin),
            vehicle: vehicle,
          };
        })
      );

      this.reservationsSubject.next(reservationsWithVehicles || []);
    } catch (error) {
      console.error(
        'VehicleReservationsService: Error loading reservations:',
        error
      );
      this.reservationsSubject.next([]);
    } finally {
      this.loadingSubject.next(false);
    }
  }

  async addReservation(reservation: any): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleReservation.createReservation);
    this.loadingSubject.next(true);

    try {
      await firstValueFrom(this.http.post<ApiResponse<any>>(url, reservation));

      await this.loadReservations();
    } catch (error: any) {
      console.error(
        'VehicleReservationsService: Error adding reservation:',
        error
      );
      if (error?.error) {
        console.error('Détail erreur backend:', error.error);
      }
      throw error;
    } finally {
      this.loadingSubject.next(false);
    }
  }

  async updateReservation(reservation: any): Promise<void> {
    const url = apiUrl(
      ApiRoutes.vehicleReservation.updateReservation(reservation)
    );
    this.loadingSubject.next(true);

    try {
      await firstValueFrom(
        this.http.put<ApiResponse<VehicleReservationDto>>(url, reservation)
      );
      await this.loadReservations();
    } catch (error) {
      console.error(
        'VehicleReservationsService: Error updating reservation:',
        error
      );
    } finally {
      this.loadingSubject.next(false);
    }
  }

  async deleteReservation(id: number): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleReservation.deleteReservation(id));
    this.loadingSubject.next(true);

    try {
      await firstValueFrom(this.http.delete<ApiResponse<any>>(url));
      await this.loadReservations();
    } catch (error) {
      console.error(
        'VehicleReservationsService: Error deleting reservation:',
        error
      );
    } finally {
      this.loadingSubject.next(false);
    }
  }
}
