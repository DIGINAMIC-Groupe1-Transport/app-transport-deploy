import { CommonModule } from '@angular/common';
import { Dialog } from '@angular/cdk/dialog'
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CarpoolDTO } from 'app/shared/models/carpool/carpool.dto';
import { CoordinatesDTO } from 'app/shared/models/carpool/coordinates.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';
import { GeolocationService } from 'app/shared/services/geolocation.service';
import { debounceTime, Subject, switchMap, takeUntil } from 'rxjs';
import { DetailsModalComponent } from '../details/details.modal.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [MatDialogModule, MatTableModule, MatPaginatorModule, MatPaginator, FormsModule, MatPaginatorModule, MatSort, MatSortModule, CommonModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  private startCoordinatesSearch$ = new Subject<string>(); //test
  private endCoordinatesSearch$ = new Subject<string>(); //test

  selected: any = null; //test

  startCoordinatesSearchQuery: string = '';
  endCoordinatesSearchQuery: string = '';

  startCoordinatesSuggestions: CoordinatesDTO[] = [];
  endCoordinatesSuggestions: CoordinatesDTO[] = [];

  selectedStartCoordinates!: CoordinatesDTO;
  selectedEndCoordinates!: CoordinatesDTO;

  date: string = new Date().toLocaleDateString('en-CA');

  showSuggestions: boolean = false;

  searchedCarpools: CarpoolDTO[] = [];
  carpoolDetails: CarpoolDTO[] = [];
  dataSource = new MatTableDataSource<CarpoolDTO>([]);
  displayedColumns: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private carpoolsService: CarpoolsService,
    private search: GeolocationService,
    private dialog: Dialog
  ) { }


  ngOnInit(): void {

    // this.initData();

    this.startCoordinatesSearch$
      .pipe(debounceTime(300), switchMap(query => this.search.search(query)))
      .subscribe(results => {
        this.startCoordinatesSuggestions = results;
      });

    this.endCoordinatesSearch$
      .pipe(debounceTime(300), switchMap(query => this.search.search(query)))
      .subscribe(results => {
        this.endCoordinatesSuggestions = results;
      });
  }

  private async initData(): Promise<void> {
    await this.loadData();
  }

  private async loadData(startX?: number, startY?: number, endX?: number, endY?: number, departureDate?: string): Promise<void> {
    try {
      //await eventuellement
      this.carpoolsService.loadSearchedCarpools(startX, startY, endX, endY, departureDate);

      this.carpoolsService.searchedCarpools$.pipe(
        takeUntil(this.destroy$)
      ).subscribe(carpools => {
        this.searchedCarpools = carpools;
        this.dataSource.data = carpools;
      });

    } catch (error) {
      console.error('OrganizeComponent: Error loading data:', error);
    }
  }

  onStartCoordinatesSearchQueryChange(): void {
    if (this.startCoordinatesSearchQuery.length < 10) {
      this.startCoordinatesSuggestions = [];
      return;
    }

    this.search.search(this.startCoordinatesSearchQuery).subscribe({
      next: (results) => (this.startCoordinatesSuggestions = results),
      error: (err) => console.error(err),
    });
  }

  onEndCoordinatesSearchQueryChange(): void {
    if (this.endCoordinatesSearchQuery.length < 10) {
      this.endCoordinatesSuggestions = [];
      return;
    }

    this.search.search(this.endCoordinatesSearchQuery).subscribe({
      next: (results) => (this.endCoordinatesSuggestions = results),
      error: (err) => console.error(err),
    });
  }

  selectStartCoordinatesSuggestions(suggestion: CoordinatesDTO): void {
    this.startCoordinatesSearchQuery = suggestion.label;
    this.startCoordinatesSuggestions = [];
    this.selectedStartCoordinates = suggestion;
  }

  selectEndCoordinatesSuggestions(suggestion: CoordinatesDTO): void {
    this.endCoordinatesSearchQuery = suggestion.label;
    this.endCoordinatesSuggestions = [];
    this.selectedEndCoordinates = suggestion;

  }

  onSearch(): void {
    const startX = this.selectedStartCoordinates.x;
    const startY = this.selectedStartCoordinates.y;
    const endX = this.selectedEndCoordinates.x;
    const endY = this.selectedEndCoordinates.y;

    this.loadData(startX, startY, endX, endY, this.date)

  }

  openModalAsReservation(carpoolId: number): void {

    this.dialog.open(DetailsModalComponent, {
      data: {
        carpoolId: carpoolId,
        mode: 'participate'
      }
    }).closed.subscribe(result => {
      if (result !== true) {
        return;
      } else {

        this.carpoolsService.participateCarpool(carpoolId).subscribe({
          next: (response) => {
            if (response) {
              console.log('Covoiturage créé avec succès:', response);
            } else {
              console.error('Échec de la création du covoiturage');
            }
          },
          error: (error) => {
            console.error('Erreur lors de la création:', error);
          }
        });

      }

    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
