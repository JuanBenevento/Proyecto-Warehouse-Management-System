import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService} from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { showBackendError } from '../../../shared/utils/error-handler';
import { User } from '../../../core/models/user.model';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.html'
})
export class UserManagementComponent implements OnInit {
  
  private userService = inject(UserService);
  public authService = inject(AuthService); // Para saber quién soy

  users: User[] = [];
  roles: string[] = ['ADMIN', 'OPERATOR']; // Roles disponibles para crear
  
  // Estado del Modal
  isModalOpen = false;
  isEditing = false;

  // Modelo del Formulario
  currentUser: User = this.getEmptyUser();

  ngOnInit() {
    this.loadUsers();
    // Opcional: Cargar roles del backend si el endpoint existe
    // this.userService.getRoles().subscribe(r => this.roles = r);
  }

  loadUsers() {
    this.userService.getUsers().subscribe({
      next: (data) => {
        // Filtro de Seguridad: No mostrar al SUPER_ADMIN en la lista de una empresa
        // Y opcionalmente no mostrarse a sí mismo para evitar auto-borrado
        this.users = data.filter(u => u.role !== 'SUPER_ADMIN');
      },
      error: (err) => showBackendError(err, 'Error cargando personal')
    });
  }

  // --- ACCIONES DEL MODAL ---

  openCreate() {
    this.currentUser = this.getEmptyUser();
    this.isEditing = false;
    this.isModalOpen = true;
  }

  openEdit(user: User) {
    this.currentUser = { ...user, password: '' }; 
    this.isEditing = true;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  save() {
    if (this.isEditing && this.currentUser.id) {
      this.userService.updateUser(this.currentUser.id, this.currentUser).subscribe({
        next: () => this.onSuccess('Usuario actualizado'),
        error: (err) => showBackendError(err, 'Error al actualizar')
      });
    } else {
      this.userService.createUser(this.currentUser).subscribe({
        next: () => this.onSuccess('Usuario creado correctamente'),
        error: (err) => showBackendError(err, 'Error al crear')
      });
    }
  }

  delete(user: User) {
    Swal.fire({
      title: '¿Dar de baja?',
      text: `Se eliminará el acceso de ${user.username}.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#ef4444',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed && user.id) {
        this.userService.deleteUser(user.id).subscribe({
          next: () => {
            this.loadUsers();
            Swal.fire('Eliminado', 'El usuario ha sido dado de baja.', 'success');
          },
          error: (err) => showBackendError(err, 'No se pudo eliminar')
        });
      }
    });
  }

  private onSuccess(msg: string) {
    this.closeModal();
    this.loadUsers();
    const Toast = Swal.mixin({
      toast: true, position: 'top-end', showConfirmButton: false, timer: 3000, timerProgressBar: true
    });
    Toast.fire({ icon: 'success', title: msg });
  }

  private getEmptyUser(): User {
    return { username: '', password: '', role: 'OPERATOR' };
  }
}