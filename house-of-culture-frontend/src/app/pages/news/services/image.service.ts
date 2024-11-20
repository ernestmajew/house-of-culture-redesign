import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {catchError, concatMap, map, Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  constructor(private httpClient: HttpClient) {
  }

  headers = new HttpHeaders()
    .set('Accept', 'image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8')

  getImage(url: string): Observable<Blob> {
    return of(url).pipe(
      concatMap((imageUrl) => {
        return this.httpClient.get(imageUrl, { responseType: 'blob', headers: this.headers }).pipe(
          catchError(error => {
            console.error('Error:', error);
            throw error;
          })
        );
      })
    );
  }
}
