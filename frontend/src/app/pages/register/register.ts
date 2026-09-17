import { Component } from '@angular/core';
import { RegisterForm } from '../../components/register-form/register-form-component';

@Component({
  imports: [RegisterForm],
  selector: 'app-register',
  styleUrl: './register.scss',
  templateUrl: './register.html',
})
export class Register {}
