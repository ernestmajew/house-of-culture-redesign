import { Pipe, PipeTransform } from '@angular/core';
import {UserInfoTs, UserTs} from "../../../../out/api";

@Pipe({
  name: 'fullname',
  standalone: true
})
export class FullnamePipe implements PipeTransform {

  transform(user: UserTs | UserInfoTs): string {
    return user.firstName + ' ' + user.lastName;
  }

}
