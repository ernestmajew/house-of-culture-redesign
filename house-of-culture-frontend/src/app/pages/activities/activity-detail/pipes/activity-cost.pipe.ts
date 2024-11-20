import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'activityCost'
})
export class ActivityCostPipe implements PipeTransform {
  transform(value: number | undefined): string {
    if(value)
      return `${value} zł`

    return "Darmowe"
  }
}
