import { Component, OnDestroy, OnInit } from '@angular/core';
import { CarpoolsService } from '../../../shared/services/carpools.service';
import { debounceTime, Subject, switchMap, takeUntil } from 'rxjs';
import { CarpoolDTO } from '../../../shared/models/carpool/carpool.dto';
import { GeolocationService } from 'app/shared/services/geolocation.service';
import { CoordinatesDTO } from 'app/shared/models/carpool/coordinates.dto';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { OrganizeComponent } from './organize/organize.component';
import { ParticipateComponent } from './participate/participate.component';
import { CoordinatesSearchComponent } from 'app/shared/components/coordinates-search/coordinates-search.component';
import { VehicleComponent } from "./organize/vehicle/vehicle.component";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [OrganizeComponent, ParticipateComponent, FormsModule, CommonModule, VehicleComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  private search$ = new Subject<string>(); //test
  selected: any = null; //test

  organizedCarpools: CarpoolDTO[] = [];
  participateCarpools: CarpoolDTO[] = [];

  query: string = '';
  suggestions: CoordinatesDTO[] = [];
  showSuggestions: boolean = false;
  

  constructor(private carpoolsService: CarpoolsService,
    private search: GeolocationService //test
  ) { }

  carpoolsMode: string = 'organize';

  ngOnInit(): void {

    this.search$ //test
      .pipe(debounceTime(300), switchMap(query => this.search.search(query))) //test
      .subscribe(results => {
        console.log('Résultats API Adresse :', results);
        console.log('Features:', results); // ← Debug
        this.suggestions = results;
      })
  }

  onSearchChange(): void {
    if (this.query.length < 3) {
      this.suggestions = [];
      return;
    }

    this.search.search(this.query).subscribe({
      next: (results) => (this.suggestions = results),
      error: (err) => console.error(err),
    });
  }


  selectAddress(addr: any) { //test
    this.selected = addr; //test
    this.suggestions = []; //test
  }

  selectSuggestion(suggestion: CoordinatesDTO): void {
    this.query = suggestion.label;
    this.suggestions = [];
  }

  changeCarpoolsMode(mode: 'vehicle' | 'organize' | 'participate'): void {
    this.carpoolsMode = mode;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
