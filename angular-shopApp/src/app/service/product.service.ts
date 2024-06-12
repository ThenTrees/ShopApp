import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Product } from '../models/product.model';
import { Injectable } from '@angular/core';
@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiGetProducts = `${environment.apiBaseUrl}/products`;
  private apiGetProductDetail = `${environment.apiBaseUrl}/products/`;
  constructor(private http: HttpClient) {}

  getProducts(
    keyword: string,
    categoryId: number,
    page: number,
    limit: number
  ): Observable<Product[]> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('category_id', categoryId)
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<Product[]>(this.apiGetProducts, {
      params,
    });
  }

  getProductDetail(productId: number) {
    return this.http.get(`${environment.apiBaseUrl}/products/${productId}`);
  }

  getProductsByIds(ids: number[]): Observable<Product[]> {
    // chuyển danh sách id thành chuỗi rồi truyền vào params

    const params = new HttpParams().set('ids', ids.join(','));
    return this.http.get<Product[]>(`${this.apiGetProducts}/by-ids`, {
      params,
    });
  }
}
