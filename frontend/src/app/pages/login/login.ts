import { Component } from '@angular/core';
import { LoginForm } from '../../components/login-form/login-form-component';

@Component({
  imports: [LoginForm],
  selector: 'app-login',
  styleUrl: './login.scss',
  templateUrl: './login.html',
})
export class Login {}
