import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LocationService } from '../../../core/services/location.service';
import { Location } from '../../../core/models/location.model';
import { showBackendError } from '../../../shared/utils/error-handler';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-location-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './location-create.html'
})
export class LocationCreateComponent implements OnInit {
  
  private locationService = inject(LocationService);
  
  locations: Location[] = [];
  
  currentLoc: Location = this.getEmptyLoc();
  isEditing = false;
  
  zones = ['DRY_STORAGE', 'COLD_STORAGE', 'FROZEN_STORAGE', 'HAZMAT', 'DOCK_DOOR'];

  private Toast = Swal.mixin({
    toast: true, position: 'top-end', showConfirmButton: false, timer: 2000, timerProgressBar: true
  });

  ngOnInit() {
    this.loadLocations();
  }

  loadLocations() {
    this.locationService.getLocations().subscribe({
      next: (data) => {
        this.locations = data.sort((a, b) => a.locationCode.localeCompare(b.locationCode));
      },
      error: (err) => showBackendError(err, 'Error cargando mapa')
    });
  }

  save() {
    const request = this.isEditing
      ? this.locationService.updateLocation(this.currentLoc.locationCode, this.currentLoc)
      : this.locationService.createLocation(this.currentLoc);

    request.subscribe({
      next: () => {
        this.Toast.fire({ icon: 'success', title: this.isEditing ? 'Ubicación actualizada' : 'Ubicación creada' });
        this.loadLocations();
        this.cancelEdit();
      },
      error: (err) => showBackendError(err, 'Error al guardar')
    });
  }

  delete(code: string) {
    Swal.fire({
      title: '¿Eliminar Ubicación?',
      text: `Estás a punto de borrar la posición ${code}. Solo es posible si está vacía.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.locationService.deleteLocation(code).subscribe({
          next: () => {
            this.Toast.fire({ icon: 'success', title: 'Ubicación eliminada' });
            this.loadLocations();
          },
          error: (err) => showBackendError(err, 'No se pudo eliminar') // Aquí saldrá el error de tu backend si tiene stock
        });
      }
    });
  }

  edit(loc: Location) {
    this.currentLoc = { ...loc }; 
    this.isEditing = true;
  }

  cancelEdit() {
    this.currentLoc = this.getEmptyLoc();
    this.isEditing = false;
  }

  private getEmptyLoc(): Location {
    return { 
      locationCode: '', 
      zoneType: 'DRY_STORAGE', 
      maxWeight: 1000, 
      maxVolume: 2000000, 
      currentWeight: 0, 
      currentVolume: 0 
    };
  }
}