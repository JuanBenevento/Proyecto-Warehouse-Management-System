import Swal from 'sweetalert2';

export function showBackendError(err: any, title: string = 'Error de Operación') {
  let htmlMessage = 'Ocurrió un error inesperado.';
  let icon: 'error' | 'warning' = 'error';
  let confirmText = 'Cerrar';

  if (err.status === 409) {
    title = 'Datos Desactualizados';
    htmlMessage = 'El registro fue modificado por otro usuario mientras usted trabajaba.<br><b>Por favor, actualice la lista e intente nuevamente.</b>';
    icon = 'warning';
    confirmText = 'Entendido';
  } 
  else if (err.error && err.error.message) {
    let rawMsg = err.error.message;
    if (rawMsg.startsWith('{') && rawMsg.endsWith('}')) {
      rawMsg = rawMsg.substring(1, rawMsg.length - 1);
      const errors = rawMsg.split(',').map((e: string) => e.trim());
      htmlMessage = `<ul style="text-align: left; margin-left: 10px;">
        ${errors.map((e: string) => `<li>${e}</li>`).join('')}
      </ul>`;
    } else {
      htmlMessage = rawMsg;
    }
  } else if (typeof err.error === 'string') {
    htmlMessage = err.error;
  }

  Swal.fire({
    title: title,
    html: htmlMessage,
    icon: icon,
    confirmButtonColor: err.status === 409 ? '#f59e0b' : '#d33', 
    confirmButtonText: confirmText
  });
}