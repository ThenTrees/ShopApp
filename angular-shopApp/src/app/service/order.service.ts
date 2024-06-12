import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../environments/environment';
import { Inject, Injectable } from '@angular/core';
import { OrderDTO } from '../dtos/order/order.dto';
import { Observable } from 'rxjs';
import { DOCUMENT } from '@angular/common';
@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private apiOrderService = `${environment.apiBaseUrl}/orders`;
  private apiConfig = {
    headers: this.createHeaders(),
  };
  localStorage?: Storage;
  constructor(
    private http: HttpClient,
    @Inject(DOCUMENT) private document: Document
  ) {
    this.localStorage = document.defaultView?.localStorage;
  }

  private createHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${this.localStorage?.getItem('access_token')}`,
      // 'Accept-Languages': 'vi',
    });
  }
  placeOrder(orderData: OrderDTO): Observable<any> {
    debugger;
    // Gửi yêu cầu đặt hàng
    return this.http.post(this.apiOrderService, orderData, this.apiConfig);
  }

  getOrderById(orderId: number): Observable<any> {
    const url = `${environment.apiBaseUrl}/orders/${orderId}`;
    return this.http.get(url);
  }
}
