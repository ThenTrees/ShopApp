import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Category } from '../models/category.model';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiGetAllCategories = `${environment.apiBaseUrl}/categories`;
  constructor(private http: HttpClient) {}
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiGetAllCategories);
  }
}
