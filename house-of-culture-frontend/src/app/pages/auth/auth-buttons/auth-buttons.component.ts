import {Component, effect, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import {AuthService} from "../services/auth.service";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {JwtTokenService} from "../services/jwt-token.service";
import {Configuration} from "../../../../../out/api";
import {Router} from "@angular/router";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {MatMenuModule} from "@angular/material/menu";

@Component({
  selector: 'app-auth-buttons',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule, MatMenuModule],
  templateUrl: './auth-buttons.component.html',
  styleUrls: ['./auth-buttons.component.scss']
})
export class AuthButtonsComponent  implements OnInit {
  constructor(private authService: AuthService,
              private jwtTokenService: JwtTokenService,
              private apiConfiguration: Configuration,
              private router: Router,
              private http: HttpClient,
  ) { }

  loggedUserToken = this.jwtTokenService.jwtToken;

  ngOnInit(): void {

  }

  myAccount() {
    this.router.navigate(['/account']);
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.router.navigate(['/']);
    this.jwtTokenService.clearToken();
  }

}
