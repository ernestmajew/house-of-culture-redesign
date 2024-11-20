import {Directive, effect, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {UserRoleTs} from "../../../../../out/api";
import {JwtTokenService} from "../../../pages/auth/services/jwt-token.service";

@Directive({
  selector: '[appAccess]',
  standalone: true
})
export class AccessDirective implements OnInit{
  @Input() appAccess!: UserRoleTs;

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private jwtTokenService: JwtTokenService
  ) {
    effect(() =>{
      this.jwtTokenService.jwtToken()
      this.checkAccess();
    })
  }

  ngOnInit(){
    this.checkAccess();
  }

  private checkAccess() {
    const decodedToken = this.jwtTokenService.decodedToken;

    if((decodedToken && this.appAccess && decodedToken.roles.includes(this.appAccess.toString()))){
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }

  }
}
