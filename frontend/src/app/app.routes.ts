import { Routes } from '@angular/router';
import { Register } from './pages/register/register';
import { Login } from './pages/login/login';

export const routes: Routes = [
    {
    path: 'login',
    component: Login
  },
  {
    path: 'register',
    component: Register
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  }
];
