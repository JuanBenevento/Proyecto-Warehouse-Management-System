export interface AuditLog {
  id: number;
  timestamp: string;
  type: 'RECEPCION' | 'AJUSTE' | 'MOVIMIENTO' | 'SALIDA';
  sku: string;
  lpn: string;
  quantity: number;
  oldQuantity: number;
  newQuantity: number;
  user: string;
  reason: string;
}