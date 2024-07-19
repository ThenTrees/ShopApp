import { ProductImage } from './productImage.model';

export interface Product {
  id: number;
  name: string;
  price: number;
  thumbnail: string;
  description: string;
  category_id: number;
  url: string;
  productImages: ProductImage[];
}
