import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeclareComponent } from './declare.component';

describe('DeclareComponent', () => {
  let component: DeclareComponent;
  let fixture: ComponentFixture<DeclareComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeclareComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeclareComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
