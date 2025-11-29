export interface Dimensions {
  width: number;
  height: number;
  depth: number;
  weight: number;
  heavyLoad?: boolean; 
}

export interface Product {
  id?: string; 
  sku: string;
  name: string;
  description: string;
  dimensions: Dimensions;
  storageVolume?: number;
}