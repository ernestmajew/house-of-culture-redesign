import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'activityDate'
})
export class ActivityDatePipe implements PipeTransform {

  transform(startDate: string, endDate: string): string {
    const startDateObj = new Date(startDate);
    const endDateObj = new Date(endDate);
    if(startDateObj.getDate() === endDateObj.getDate()){
      return startDateObj.toLocaleDateString();
    }else{
      return startDateObj.toLocaleDateString() + " - " + endDateObj.toLocaleDateString();
    }
  }

}
