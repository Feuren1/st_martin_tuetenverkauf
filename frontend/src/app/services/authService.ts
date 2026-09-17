import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Service } from '@angular/core';
import { LoginRequest, RegisterRequest, LoginResponse } from '../model/auth';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient)

  register(registerRequest: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/api/auth/register`, registerRequest);
  }

  login(loginRequest: LoginRequest) {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/api/auth/login`, loginRequest)
  }

}
