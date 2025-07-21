import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({ providedIn: 'root' })
export class SnackbarService {
  private snackBar = inject(MatSnackBar);

  show(message: string, durationMs = 3000) {
    this.snackBar.open(message, 'Fermer', {
      duration: durationMs,
      horizontalPosition: 'right',
      verticalPosition: 'top',
      panelClass: ['debug-snackbar'], 
    });
  }
}
