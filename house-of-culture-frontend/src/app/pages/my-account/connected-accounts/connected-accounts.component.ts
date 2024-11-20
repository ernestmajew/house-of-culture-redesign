import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MatDividerModule} from "@angular/material/divider";
import {UserService} from "../service/user.service";
import {UserInfoTs} from "../../../../../out/api";
import {AccountCardComponent} from "./account-card/account-card.component";

@Component({
  selector: 'app-connected-accounts',
  standalone: true,
  imports: [CommonModule, MatDividerModule, AccountCardComponent],
  templateUrl: './connected-accounts.component.html',
  styleUrls: ['./connected-accounts.component.scss']
})
export class ConnectedAccountsComponent {
  children: UserInfoTs[] = [];

  constructor(
    private service: UserService
  ) {
  }

  ngOnInit(): void {
    this.service.getChildren().subscribe(
      response => {
        this.children = response;
      }
    )
  }

}
