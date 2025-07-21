
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { CarpoolDTO } from 'app/shared/models/carpool/carpool.dto';
import { CoordinatesDTO } from 'app/shared/models/carpool/coordinates.dto';
import { CarpoolsService } from 'app/shared/services/carpools.service';
import { GeolocationService } from 'app/shared/services/geolocation.service';
import { debounceTime, Subject, switchMap, takeUntil } from 'rxjs';


@Component({
  selector: 'app-coordinates-search',
  standalone: true,
  imports: [MatTableModule, MatPaginatorModule, FormsModule, MatPaginatorModule, MatSortModule, CommonModule],
  templateUrl: './coordinates-search.component.html',
  styleUrl: './coordinates-search.component.css'
})

export class CoordinatesSearchComponent implements OnInit, OnDestroy {

  private destroy$ = new Subject<void>();

  @Output() startCoordinatesSelectedEvent = new EventEmitter<CoordinatesDTO>();
  @Output() endCoordinatesSelectedEvent = new EventEmitter<CoordinatesDTO>();

  private startCoordinatesSearch$ = new Subject<string>();
  private endCoordinatesSearch$ = new Subject<string>();

  selected: any = null;

  startCoordinatesSearchQuery: string = '';
  endCoordinatesSearchQuery: string = '';

  startCoordinatesSuggestions: CoordinatesDTO[] = [];
  endCoordinatesSuggestions: CoordinatesDTO[] = [];

  selectedStartCoordinates!: CoordinatesDTO;
  selectedEndCoordinates!: CoordinatesDTO;

  date: string = new Date().toLocaleDateString('en-CA');

  showSuggestions: boolean = false;

  searchedCarpools: CarpoolDTO[] = [];

  dataSource = new MatTableDataSource<CarpoolDTO>([]);
  displayedColumns: string[] = ['startLabel', 'endLabel', 'estimatedDepartureTime', 'estimatedArrivalTime', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private search: GeolocationService
  ) { }


  ngOnInit(): void {

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
    this.startCoordinatesSelectedEvent.emit(suggestion);
  }

  selectEndCoordinatesSuggestions(suggestion: CoordinatesDTO): void {
    this.endCoordinatesSearchQuery = suggestion.label;
    this.endCoordinatesSuggestions = [];
    // this.selectedEndCoordinates = suggestion;
    this.endCoordinatesSelectedEvent.emit(suggestion);
  }


  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
