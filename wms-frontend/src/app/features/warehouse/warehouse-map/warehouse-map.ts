import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LocationService} from '../../../core/services/location.service';
import { Location } from '../../../core/models/location.model';

@Component({
  selector: 'app-warehouse-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './warehouse-map.html',
  styles: [`
    .location-card { transition: transform 0.2s; }
    .location-card:hover { transform: scale(1.05); cursor: pointer; }
  `]
})
export class WarehouseMapComponent implements OnInit {
  
  private locationService = inject(LocationService);
  locations: Location[] = [];

  ngOnInit() {
    this.loadMap();
  }

  loadMap() {
    this.locationService.getLocations().subscribe(data => {
      this.locations = data.sort((a, b) => a.locationCode.localeCompare(b.locationCode));
    });
  }

  getOccupancyPercentage(loc: Location): number {
    if (loc.maxWeight === 0) return 0;
    const pct = (loc.currentWeight / loc.maxWeight) * 100;
    return Math.min(pct, 100); 
  }

  getProgressColor(pct: number): string {
    if (pct < 50) return 'bg-success'; 
    if (pct < 90) return 'bg-warning';
    return 'bg-danger';               
  }
}