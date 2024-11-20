import {Component, OnInit} from '@angular/core';
import {FacebookAuthenticationService} from "../services/facebook-authentication.service";
import {ActivatedRoute, Router} from "@angular/router";
import {FacebookUserCodeResponseTs} from "../../../../../out/api/model/facebook-user-code-response";
import {catchError} from "rxjs";
import {MessageService} from "../../../core/services/message.service";
import {NotificationMessageType} from "../../../core/models/notification-message";
import {MatDialog} from "@angular/material/dialog";
import {UserCodeDialogComponent} from "./user-code-dialog/user-code-dialog.component";
import {FacebookAuthenticationDataResponseTs} from "../../../../../out/api/model/facebook-authentication-data-response";
import {PageConnectionDialogComponent} from "./page-connection-dialog/page-connection-dialog.component";
import {ConnectFacebookPageRequestTs} from "../../../../../out/api/model/connect-facebook-page-request";

@Component({
  selector: 'app-social-media',
  templateUrl: './social-media.component.html',
  styleUrls: ['./social-media.component.scss']
})
export class SocialMediaComponent implements OnInit{
  authenticationData?: FacebookAuthenticationDataResponseTs
  isUserConnected = false
  isPageConnected = false
  isInstagramConnected = false

  constructor(
    private authenticationService: FacebookAuthenticationService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private messageService: MessageService,
    private dialog: MatDialog
  ) {
  }

  ngOnInit(): void {
    this.authenticationService.getFacebookAuthenticationData()
      .pipe(catchError(err => {
        if(err.status == 404) {

        } else {
          this.messageService.sendMessage("Coś poszło nie tak. Dane nie zostały pobrane.", NotificationMessageType.ERROR)
        }

        return err
      }))
      .subscribe(response => {
        this.setAuthenticationData(response)
      })
  }

  backToPreviousPage() {
    this.router.navigate(["../../"], {relativeTo: this.activatedRoute});
  }

  authenticateFacebookApi() {
    this.authenticationService.authenticateFacebookApi().pipe(
      catchError((err) => {
        this.messageService.sendMessage(
            "Uwierzytelnianie nie udało się. Spróbuj ponownie później.",
            NotificationMessageType.ERROR
        )

        return err
      })
    ).subscribe((response: FacebookUserCodeResponseTs) => {
      this.dialog.open(UserCodeDialogComponent, {
        data: {
          code: response.code,
          verificationUri: response.verificationUri
        },
        width: "500px"
      })
    })
  }

  openConnectPageDialog() {
    this.authenticationService.getAvailableFacebookPages()
      .pipe(catchError((err) => {
          let message = err.status == 409
            ? "Przed połączeniem strony połącz swoje konto Facebook"
            : "Coś poszło nie tak. Spróbuj ponownie później."

          this.messageService.sendMessage(message, NotificationMessageType.ERROR)

          return err
        })
      ).subscribe((pages) => {
        this.dialog.open(PageConnectionDialogComponent, {
          data: pages,
          width: "500px"
        }).afterClosed().subscribe((result: ConnectFacebookPageRequestTs) => {
          if(result) this.connectSelectedPage(result)
        })
    })
  }

  connectSelectedPage(request: ConnectFacebookPageRequestTs) {
    this.authenticationService.connectFacebookPage(request)
      .pipe(catchError((err) => {
        let message = "Coś poszło nie tak. Spróbuj ponownie później."

        if(err.status == 404) {
          message = "Wybrana strona nie istnieje. Mogła zostać usunięte."
        }else if(err.status == 409) {
          message = "Podane konto Instagram nie jest połączone do wybranej strony."
        }else if(err.status == 422) {
          message = "Konto użytkownika Facebook nie zostało jeszcze połączone."
        }

        this.messageService.sendMessage(message, NotificationMessageType.ERROR)

        return err
      }))
      .subscribe((response) => {
        this.messageService.sendMessage(
          "Pomyślnie połączono stronę z aplikacją.",
          NotificationMessageType.SUCCESS
        )
        this.setAuthenticationData(response)
      })
  }

  setAuthenticationData(newData:  FacebookAuthenticationDataResponseTs) {
    this.authenticationData = newData
    this.isUserConnected = this.authenticationData !== undefined
    this.isPageConnected = this.authenticationData?.pageName !== null
    this.isInstagramConnected = this.authenticationData?.instagramUsername !== null
  }
}
