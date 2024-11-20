import {Injectable} from '@angular/core';
import {FacebookAuthenticationApiService} from "../../../../../out/api/api/facebook-authentication.service";
import {ConnectFacebookPageRequestTs} from "../../../../../out/api/model/connect-facebook-page-request";

@Injectable({
  providedIn: 'root'
})
export class FacebookAuthenticationService {

  constructor(
    private service: FacebookAuthenticationApiService
  ) {}

  getFacebookAuthenticationData() {
    return this.service.getFacebookAuthenticationData()
  }

  authenticateFacebookApi() {
    return this.service.authenticateFacebookApi()
  }

  getAvailableFacebookPages() {
    return this.service.getAvailableFacebookPages()
  }

  connectFacebookPage(request: ConnectFacebookPageRequestTs) {
    return this.service.connectFacebookPage({connectFacebookPageRequestTs: request})
  }
}
