import {inject, Injectable} from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import {JwtTokenService} from "../../pages/auth/services/jwt-token.service";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const service = inject(JwtTokenService);
    const token = service.getToken();

    if (token && service.isTokenExpired(token)) {
        service.refreshToken();
    }

    return next.handle(req);
  }
}
