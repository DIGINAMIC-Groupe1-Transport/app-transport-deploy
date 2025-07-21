import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  BehaviorSubject,
  catchError,
  finalize,
  firstValueFrom,
  Observable,
  of,
  tap,
} from 'rxjs';
import { map } from 'rxjs/operators';
import { apiUrl } from '../../core/api/api-url-builder';
import { ApiRoutes } from '../../core/api/api-routes';
import { ApiResponse, PaginatedResponse } from '../models/response.model';
import { VehicleDTO } from '../models/vehicle/vehicle.dto';

@Injectable({
  providedIn: 'root',
})
export class VehiclesService {
  private personalVehiclesSubject = new BehaviorSubject<VehicleDTO[]>([]);
  public personalVehicles$ = this.personalVehiclesSubject.asObservable();

  private vehiclesSubject = new BehaviorSubject<VehicleDTO[]>([]);
  public vehicles$ = this.vehiclesSubject.asObservable();

  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  constructor(private http: HttpClient) {}

   loadPersonalVehicles(): Observable<ApiResponse<PaginatedResponse<VehicleDTO>>> {
    const url = apiUrl(ApiRoutes.vehicle.getPersonalVehicles);

    return this.http.get<ApiResponse<PaginatedResponse<VehicleDTO>>>(url).pipe(
      tap(response => {
        this.personalVehiclesSubject.next(response.data.content);
      }),
    )
  }

  createPersonalVehicle(
    vehicle: VehicleDTO
  ): Observable<ApiResponse<VehicleDTO>> {
    const url = apiUrl(ApiRoutes.vehicle.createPersonalVehicle);

    return this.http.post<ApiResponse<VehicleDTO>>(url, vehicle).pipe(
      tap((response) => {
        const current = this.personalVehiclesSubject.value;
        this.personalVehiclesSubject.next([...current, response.data]);
      })
    );
  }

  deletePersonalVehicle(vehicleId: number): Observable<ApiResponse<void>> {
    const url = apiUrl(
      ApiRoutes.vehicle.deletePersonalVehicle.replace(
        '{id}',
        vehicleId.toString()
      )
    );

    return this.http.delete<ApiResponse<void>>(url).pipe(
      tap((response) => {
        const current = this.personalVehiclesSubject.value;
        this.personalVehiclesSubject.next(
          current.filter((v) => v.id !== vehicleId)
        );
      })
    );
  }

  // Method to load all vehicles
  async loadVehicles(): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleService.getAllVehicles);
    this.loadingSubject.next(true);
    try {
      const response = await firstValueFrom(this.http.get<any>(url));

      // Extraire les données depuis la propriété data
      let vehicles: any[] = [];
      if (response && response.status === 'success' && response.data) {
        vehicles = Array.isArray(response.data) ? response.data : [];
      }

      this.vehiclesSubject.next(vehicles);
    } catch (error) {
      console.error('VehiclesService: Error loading vehicles:', error);
      this.vehiclesSubject.next([]);
    } finally {
      this.loadingSubject.next(false);
    }
  }

  async getAllVehicles(): Promise<VehicleDTO[]> {
    const url = apiUrl(ApiRoutes.vehicleService.getAllVehicles);
    try {
      const response = await firstValueFrom(this.http.get<any>(url));

      // Extraire les données depuis la propriété data
      if (response && response.status === 'success' && response.data) {
        return Array.isArray(response.data.content)
          ? response.data.content
          : [];
      }

      return [];
    } catch (error) {
      console.error('VehiclesService: Error getting all vehicles:', error);
      return [];
    }
  }

  async getVehicleById(id: number): Promise<VehicleDTO | null> {
    const url = apiUrl(ApiRoutes.vehicleService.getVehicleById(id));

    try {
      const response = await firstValueFrom(this.http.get<any>(url));

      // Extraire les données du véhicule depuis la propriété data
      if (response && response.status === 'success' && response.data) {
        const vehicleData = response.data;
        return {
          id: vehicleData.id,
          registration: vehicleData.registration,
          brand: vehicleData.brand,
          model: vehicleData.model,
          seats: vehicleData.seats,
          isCompany: vehicleData.isCompany,
          co2PerKm: vehicleData.co2PerKm,
          vehicleStatus: vehicleData.vehicleStatus,
          vehicleCategory: vehicleData.vehicleCategory,
          vehicleMotor: vehicleData.vehicleMotor,
          urlImage: vehicleData.urlImage || 'assets/img/carAccueil.png',
        };
      }

      return null;
    } catch (error) {
      console.error('VehiclesService: Error getting vehicle by id:', error);
      return null;
    }
  }

  async addVehicle(vehicle: VehicleDTO): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleService.add);
    this.loadingSubject.next(true);
    try {
      await firstValueFrom(
        this.http.post<ApiResponse<VehicleDTO>>(url, vehicle)
      );
      await this.loadVehicles();
    } catch (error) {
      console.error('VehiclesService: Error adding vehicle:', error);
    } finally {
      this.loadingSubject.next(false);
    }
  }

  async updateVehicle(id: number, vehicle: VehicleDTO): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleService.update(id));
    this.loadingSubject.next(true);
    try {
      await firstValueFrom(
        this.http.put<ApiResponse<VehicleDTO>>(url, vehicle)
      );
      await this.loadVehicles();
    } catch (error) {
      console.error('VehiclesService: Error updating vehicle:', error);
    } finally {
      this.loadingSubject.next(false);
    }
  }

  async deleteVehicle(id: number): Promise<void> {
    const url = apiUrl(ApiRoutes.vehicleService.delete(id));
    this.loadingSubject.next(true);
    try {
      await firstValueFrom(this.http.delete<ApiResponse<any>>(url));
      await this.loadVehicles();
    } catch (error) {
      console.error('VehiclesService: Error deleting vehicle:', error);
    } finally {
      this.loadingSubject.next(false);
    }
  }

  getAvailableVehicles(startDateTime: string, endDateTime: string) {
    const url = apiUrl(ApiRoutes.vehicleService.getAllVehicles);
    return this.http.get<any>(url).pipe(
      map((response) => {
        // Extraire les données depuis la propriété data
        if (response && response.status === 'success' && response.data) {
          return Array.isArray(response.data.content)
            ? response.data.content
            : [];
        }
        return [];
      })
    );
  }
}
