import { Injectable, inject } from '@angular/core';
import { HttpClientService } from '../../core/services/http-cient.service';
import { Observable } from 'rxjs';

export interface VehicleReservationDTO {
  id?: number;
  vehicleId: number;
  userId?: number;
  startDate: string; // Format ISO date
  endDate: string; // Format ISO date
  purpose: 'business' | 'personal' | 'training' | 'maintenance' | 'other';
  notes?: string;
  status?: 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  createdAt?: string;
  updatedAt?: string;
}

export interface VehicleReservationResponse {
  id: number;
  vehicle: {
    id: number;
    brand: string;
    model: string;
    registration: string;
    urlImage?: string;
  };
  user: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
  };
  startDate: string;
  endDate: string;
  purpose: string;
  notes?: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class VehicleReservationsService {
  private httpClient = inject(HttpClientService);
  private baseUrl = '/vehicle-reservations'; // À ajuster selon votre API

  /**
   * Récupère toutes les réservations de l'utilisateur connecté
   */
  getUserReservations(): Observable<VehicleReservationResponse[]> {
    return this.httpClient.get<VehicleReservationResponse[]>(`${this.baseUrl}/user`);
  }

  /**
   * Récupère toutes les réservations (admin)
   */
  getAllReservations(): Observable<VehicleReservationResponse[]> {
    return this.httpClient.get<VehicleReservationResponse[]>(`${this.baseUrl}`);
  }

  /**
   * Récupère une réservation par son ID
   */
  getReservationById(id: number): Observable<VehicleReservationResponse> {
    return this.httpClient.get<VehicleReservationResponse>(`${this.baseUrl}/${id}`);
  }

  /**
   * Crée une nouvelle réservation
   */
  createReservation(reservation: VehicleReservationDTO): Observable<VehicleReservationResponse> {
    return this.httpClient.post<VehicleReservationResponse>(`${this.baseUrl}`, reservation);
  }

  /**
   * Met à jour une réservation existante
   */
  updateReservation(id: number, reservation: Partial<VehicleReservationDTO>): Observable<VehicleReservationResponse> {
    return this.httpClient.put<VehicleReservationResponse>(`${this.baseUrl}/${id}`, reservation);
  }

  /**
   * Supprime une réservation
   */
  deleteReservation(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseUrl}/${id}`);
  }

  /**
   * Annule une réservation (change le statut)
   */
  cancelReservation(id: number): Observable<VehicleReservationResponse> {
    return this.httpClient.put<VehicleReservationResponse>(`${this.baseUrl}/${id}/cancel`, {});
  }

  /**
   * Confirme une réservation (change le statut)
   */
  confirmReservation(id: number): Observable<VehicleReservationResponse> {
    return this.httpClient.put<VehicleReservationResponse>(`${this.baseUrl}/${id}/confirm`, {});
  }

  /**
   * Récupère les réservations pour une période donnée
   */
  getReservationsByDateRange(startDate: string, endDate: string): Observable<VehicleReservationResponse[]> {
    return this.httpClient.get<VehicleReservationResponse[]>(
      `${this.baseUrl}/period?startDate=${startDate}&endDate=${endDate}`
    );
  }

  /**
   * Vérifie la disponibilité d'un véhicule pour une période
   */
  checkVehicleAvailability(vehicleId: number, startDate: string, endDate: string): Observable<boolean> {
    return this.httpClient.get<boolean>(
      `${this.baseUrl}/availability/vehicle/${vehicleId}?startDate=${startDate}&endDate=${endDate}`
    );
  }

  /**
   * Récupère les véhicules disponibles pour une période
   */
  getAvailableVehicles(startDate: string, endDate: string): Observable<any[]> {
    return this.httpClient.get<any[]>(
      `/vehicles/available?startDate=${startDate}&endDate=${endDate}`
    );
  }
}
