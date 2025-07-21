import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CoordinatesSearchComponent } from './coordinates-search.component';

describe('CoordinatesSearchComponent', () => {
  let component: CoordinatesSearchComponent;
  let fixture: ComponentFixture<CoordinatesSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoordinatesSearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CoordinatesSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
