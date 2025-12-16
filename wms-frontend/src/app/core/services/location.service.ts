import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Location } from '../models/location.model';

@Injectable({ providedIn: 'root' })
export class LocationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/v1/locations';

  getLocations(): Observable<Location[]> {
    return this.http.get<Location[]>(this.apiUrl);
  }

  createLocation(loc: Location): Observable<Location> {
    return this.http.post<Location>(this.apiUrl, loc);
  }

  updateLocation(code: string, loc: Location): Observable<Location> {
    return this.http.put<Location>(`${this.apiUrl}/${code}`, loc);
  }

  deleteLocation(code: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${code}`);
  }
}