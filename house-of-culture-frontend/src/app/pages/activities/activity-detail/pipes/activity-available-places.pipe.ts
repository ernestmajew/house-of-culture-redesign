import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'activityAvailablePlaces'
})
export class ActivityAvailablePlacesPipe implements PipeTransform {

  transform(availablePlaces?: number, maxPlaces?: number, closestAvailableDate?: string): string {
    if(maxPlaces == undefined)
      return "Brak limitu miejsc"
    else if (availablePlaces != undefined && availablePlaces > 0)
      return `${maxPlaces-availablePlaces}/${maxPlaces} zajętych miejsc`
    else if (closestAvailableDate != undefined)
      return `Wolne miejsca od ${(new Date(closestAvailableDate)).toLocaleDateString()}`
    else
      return "Brak wolnych miejsc"
  }
}
