import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../services/authService';
import { LoginRequest } from '../../model/auth';

@Component({
  imports: [ReactiveFormsModule, MatButtonModule, MatCardModule, MatFormFieldModule, MatInputModule],
  selector: 'app-login-form',
  standalone: true,
  styleUrl: './login-form-component.scss',
  templateUrl: './login-form-component.html',
})
export class LoginForm {
  private readonly formBuilder = inject(FormBuilder);
  private authService = inject(AuthService)
  protected readonly form = this.formBuilder.nonNullable.group({
    email: ['', Validators.required],
    password: ['', Validators.required],
  });

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const req: LoginRequest = {
      email: this.form.controls.email.value,
      password: this.form.controls.password.value,
    };

    this.authService.login(req).subscribe({
      next: () => {
        console.log('Login!');
      },
      error: (error) => {
        console.error('Login! failed', error);
      },
    })
  }
}
