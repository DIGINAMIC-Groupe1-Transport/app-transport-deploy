import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service'; // ajuste le chemin selon ta structure
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div style="max-width: 400px; margin: auto; padding: 2rem;">
      <h2>Login</h2>
      <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
        <div style="margin-bottom: 1rem;">
          <label for="email">Email:</label><br />
          <input
            type="email"
            id="email"
            name="email"
            required
            [(ngModel)]="corpEmail"
            #emailInput="ngModel"
          />
          <div
            *ngIf="emailInput.invalid && emailInput.touched"
            style="color: red;"
          >
            Email is required and must be valid.
          </div>
        </div>

        <div style="margin-bottom: 1rem;">
          <label for="password">Password:</label><br />
          <input
            type="password"
            id="password"
            name="password"
            required
            [(ngModel)]="password"
            #passwordInput="ngModel"
          />
          <div
            *ngIf="passwordInput.invalid && passwordInput.touched"
            style="color: red;"
          >
            Password is required.
          </div>
        </div>

        <button type="submit" [disabled]="loginForm.invalid || loading">
          Login
        </button>
      </form>

      <p
        *ngIf="message"
        [style.color]="error ? 'red' : 'green'"
        style="margin-top: 1rem;"
      >
        {{ message }}
      </p>
    </div>
  `,
})
export class LoginComponent {
  corpEmail = '';
  password = '';
  message = '';
  error = false;
  loading = false;

  constructor(private authService: AuthService) {}

  private router = inject(Router);

  onSubmit() {
    if (!this.corpEmail || !this.password) return;

    this.loading = true;
    this.message = '';
    this.error = false;

    this.authService
      .login({ corpEmail: this.corpEmail, password: this.password })
      .subscribe({
        next: (response) => {
          this.message = `Logged in successfully!`;
          this.loading = false;
          this.router.navigate(['']);

        },
        error: (err) => {
          this.message = 'Login failed.';
          this.error = true;
          this.loading = false;
          console.log('ERROR:', err);
          console.log('Status:', err.status);
          console.log('Error body:', err.error);
        },
      });
  }
}
