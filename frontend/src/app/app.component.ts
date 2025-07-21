import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule,CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'transport-angular';

  constructor(public authService: AuthService) {}

  get isAdmin(): boolean {
   
    return this.authService.isAdmin();
  }

  logout() {
    this.authService.logout();
  }
}
