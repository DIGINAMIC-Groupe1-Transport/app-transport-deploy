import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { CoordinatesDTO } from '../models/carpool/coordinates.dto';

@Injectable({
    providedIn: 'root'
})
export class GeolocationService {

    constructor(private http: HttpClient) { }

    search(query: string): Observable<CoordinatesDTO[]> {
        const url = `https://api-adresse.data.gouv.fr/search/?q=${encodeURIComponent(query)}&limit=5`;
        return this.http.get<any>(url).pipe(
            map(res => res.features.map((f: any) => ({
                label: f.properties.label,
                street: f.properties.street || '',
                houseNumber: f.properties.housenumber || '',
                city: f.properties.city || '',
                x: f.geometry.coordinates[0],
                y: f.geometry.coordinates[1]
            })))
        );
    }
}