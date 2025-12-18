export interface InventoryItem {
  lpn: string;
  productSku: string;
  quantity: number;
  status: string;
  locationCode: string;
  batchNumber: string;
  expiryDate: string; 
  createdBy?: string;
  createdDate?: string;
}