import {Injectable, signal} from '@angular/core';
import jwt_decode from 'jwt-decode';
import {Configuration} from "../../../../../out/api";
import {HttpClient} from "@angular/common/http";
import {catchError} from "rxjs";
import {Router} from "@angular/router";

export const TOKEN_NAME: string = 'jwt_token';
export const REFRESH_TOKEN_NAME: string = 'refresh_token';

export interface DecodedToken {
  sub: string;
  exp: number;
  iat: number;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class JwtTokenService {
  jwtToken = signal<string>('');

  decodedToken?: DecodedToken;
  constructor(
    private apiConfiguration: Configuration,
    private http: HttpClient,
    private router: Router,
  ) {
    this.setToken(this.getTokenOrEmpty());
  }

  getToken(){
    return localStorage.getItem(TOKEN_NAME);
  }

  getTokenOrEmpty(){
    return localStorage.getItem(TOKEN_NAME) ?? '';
  }

  setRefreshToken(refreshToken: string) {
    localStorage.setItem(REFRESH_TOKEN_NAME, refreshToken);
  }

  setTokens(token: string, refreshToken: string) {
    this.setRefreshToken(refreshToken);
    this.setTokenAndDecode(token);
  }

  setToken(token: string): void {
    if (token){
      this.setTokenAndDecode(token);

      if(this.isTokenExpired(token)){
        this.refreshToken();
      }
    }
  }

  clearToken(): void {
    this.apiConfiguration.accessToken = '';
    this.jwtToken.set('');
    localStorage.removeItem(TOKEN_NAME);
    localStorage.removeItem(REFRESH_TOKEN_NAME);
    this.decodedToken = undefined;
  }

  decodeToken() {
    if (this.jwtToken()) {
      this.decodedToken = jwt_decode(this.jwtToken());
    }
  }

  getUsername() {
    this.decodeToken();
    return this.decodedToken ? this.decodedToken.sub : null;
  }

  isTokenExpired(token: string): boolean {
    const expiryTime = (jwt_decode(token) as DecodedToken).exp;

    if (expiryTime) {
      return ((1000 * expiryTime) - (new Date()).getTime()) < 5000;
    } else {
      return false;
    }
  }


  private setTokenAndDecode(token: string) {
    this.apiConfiguration.accessToken = token;
    this.jwtToken.set(token);
    localStorage.setItem(TOKEN_NAME, token);
    this.decodeToken()
  }

   async refreshToken() {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_NAME);
    if (refreshToken && !this.isTokenExpired(refreshToken)) {
      const headers = {
        "Authorization": `Bearer ${refreshToken}}`
      }
      await this.http.post(this.apiConfiguration.basePath + '/api/auth/refresh-token',null, {headers: headers})
        .pipe(
          catchError((error) => {
            this.clearToken();
            this.router.navigate(['/login']);
            return error;
          }
        ))
        .subscribe((data: any) => {
          this.setTokenAndDecode(data.token);
        })
    }else{
      this.clearToken();
      this.router.navigate(['/login']);
    }
  }

}
