import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, catchError, finalize, firstValueFrom, Observable, of, tap } from 'rxjs';
import { CarpoolDTO } from '../models/carpool/carpool.dto';
import { apiUrl } from '../../core/api/api-url-builder';
import { ApiRoutes } from '../../core/api/api-routes';
import { ApiResponse, PaginatedResponse } from '../models/response.model';

@Injectable({
  providedIn: 'root'
})
export class CarpoolsService {

  private organizedCarpoolsSubject = new BehaviorSubject<CarpoolDTO[]>([]);
  public organizedCarpools$ = this.organizedCarpoolsSubject.asObservable();

  private participateCarpoolsSubject = new BehaviorSubject<CarpoolDTO[]>([]);
  public participateCarpools$ = this.participateCarpoolsSubject.asObservable();

  private searchedCarpoolsSubject = new BehaviorSubject<CarpoolDTO[]>([]);
  public searchedCarpools$ = this.searchedCarpoolsSubject.asObservable();


  constructor(private http: HttpClient) { }

  loadParticipateCarpools(): Observable<ApiResponse<PaginatedResponse<CarpoolDTO>>> {

    const url = apiUrl(ApiRoutes.carpool.getParticipateCarpools);

    return this.http.get<ApiResponse<PaginatedResponse<CarpoolDTO>>>(url).pipe(
      tap(response => {
        this.participateCarpoolsSubject.next(response.data.content);
      }),
    );
  }

  loadOrganizedCarpools(): Observable<ApiResponse<PaginatedResponse<CarpoolDTO>>> {
    const url = apiUrl(ApiRoutes.carpool.organizedCarpools);

    return this.http.get<ApiResponse<PaginatedResponse<CarpoolDTO>>>(url).pipe(
      tap(response => {
        this.organizedCarpoolsSubject.next(response.data.content);
      }),
    );
  }

  async loadSearchedCarpools(startX?: number, startY?: number, endX?: number, endY?: number, departureDate?: string): Promise<void> {
    const url = apiUrl(ApiRoutes.carpool.getSearchedCarpools);

    try {

      const params: { [key: string]: any } = {};

      if (startX !== undefined) params['startX'] = startX;
      if (startY !== undefined) params['startY'] = startY;
      if (endX !== undefined) params['endX'] = endX;
      if (endY !== undefined) params['endY'] = endY;
      if (departureDate !== undefined) params['departureDate'] = departureDate;

      const carpools = await firstValueFrom(this.http.get<ApiResponse<PaginatedResponse<CarpoolDTO>>>(url, { params }));
      this.searchedCarpoolsSubject.next(carpools.data.content || []);
    } catch (error) {
      console.error('CarpoolsService: Error loading carpools:', error);
      this.searchedCarpoolsSubject.next([]);
    }
  }

  createOrganizedCarpool(carpool: CarpoolDTO): Observable<ApiResponse<CarpoolDTO> | null> {
    const url = apiUrl(ApiRoutes.carpool.organizedCarpools);

    return this.http.post<ApiResponse<CarpoolDTO>>(url, carpool).pipe(
      tap(response => {
        const current = this.organizedCarpoolsSubject.value;
        this.organizedCarpoolsSubject.next([...current, response.data]);
      }),
    );
  }

  deleteOrganizedCarpool(carpoolId: number): Observable<ApiResponse<void>> {
    const url = apiUrl(ApiRoutes.carpool.deleteOrganizedCarpool.replace('{id}', carpoolId.toString()));

    return this.http.delete<ApiResponse<void>>(url).pipe(
      tap(response => {
        const current = this.organizedCarpoolsSubject.value;
        this.organizedCarpoolsSubject.next(current.filter(v => v.id !== carpoolId));
      })
    );
  }

  participateCarpool(carpoolId: number): Observable<ApiResponse<CarpoolDTO> | null> {
    const url = apiUrl(ApiRoutes.carpool.participateCarpool.replace('{id}', carpoolId.toString()));

    return this.http.put<ApiResponse<CarpoolDTO>>(url, null).pipe(
      tap(response => {
        const current = this.participateCarpoolsSubject.value;
        this.participateCarpoolsSubject.next([...current, response.data]);
      })
    );
  }

  deleteParticipateCarpool(carpoolId: number): Observable<ApiResponse<void>> {
    const url = apiUrl(ApiRoutes.carpool.deleteParticipateCarpool.replace('{id}', carpoolId.toString()));


    return this.http.delete<ApiResponse<void>>(url).pipe(
      tap(response => {
        const current = this.participateCarpoolsSubject.value;
        this.participateCarpoolsSubject.next(current.filter(v => v.id !== carpoolId));
      }),
    );
  }


  getCarpoolDetails(carpoolId: number): Observable<ApiResponse<CarpoolDTO>> {
    const url = apiUrl(ApiRoutes.carpool.getCarpoolDetails.replace('{id}', carpoolId.toString()));

    return this.http.get<ApiResponse<CarpoolDTO>>(url);
  }

}



