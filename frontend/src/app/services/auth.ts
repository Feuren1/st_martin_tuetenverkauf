import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { registerRequest } from '../model/auth';

@Service()
export class Auth {

    private http = inject(HttpClient)

    register(registerRequest: registerRequest){
        this.http.post('/api/auth/login', registerRequest)
    }
}
