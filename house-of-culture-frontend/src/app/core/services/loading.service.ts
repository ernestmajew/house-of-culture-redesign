import {Injectable, signal} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  loading = signal(false);

  constructor() {
  }

  setLoadingTrue(): void {
    this.loading.set(true)
  }

  setLoadingFalse(): void {
    this.loading.set(false)
  }

  simulateLoading(): void {
    this.setLoadingTrue();
    setTimeout(() => this.setLoadingFalse(), 3000);
  }
}
