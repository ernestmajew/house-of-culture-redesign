import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'activityAge'
})
export class ActivityAgePipe implements PipeTransform {

  transform(minAge?: number, maxAge?: number): unknown {
    if(minAge && maxAge){
      return `Od ${minAge} do ${maxAge} lat`;
    }else if(minAge){
      return minAge + "+";
    }else if(maxAge){
      return maxAge + "-";
    }else{
      return "Bez ograniczeń wiekowych";
    }
  }

}
