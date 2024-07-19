import { environment } from '../../environments/environment';
import { OrderDetail } from '../../models/order.detail.model';
import { OrderDetailResponse } from '../../responses/orderDetail/orderDetail.response';
import { OrderService } from '../../service/order.service';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
@Component({
  selector: 'app-order-confirm',
  templateUrl: './order-confirm.component.html',
  styleUrls: ['./order-confirm.component.scss'],
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
})
export class OrderConfirmComponent implements OnInit {
  orderResponse: OrderDetailResponse = {
    id: 0, // Hoặc bất kỳ giá trị số nào bạn muốn
    user_id: 0,
    fullname: '',
    phone_number: '',
    email: '',
    address: '',
    note: '',
    order_date: new Date(),
    status: '',
    total_money: 0, // Hoặc bất kỳ giá trị số nào bạn muốn
    shipping_method: '',
    shipping_address: '',
    shipping_date: new Date(),
    payment_method: '',
    order_details: [], // Một mảng rỗng
  };

  constructor(private orderService: OrderService) {}
  ngOnInit(): void {
    this.getOrderDetail();
  }

  getOrderDetail() {
    debugger;
    const orderId = 5;
    this.orderService.getOrderById(orderId).subscribe({
      next: (response: any) => {
        debugger;
        this.orderResponse.id = response.data.id;
        this.orderResponse.fullname = response.data.fullname;
        this.orderResponse.email = response.data.email;
        this.orderResponse.address = response.data.address;
        this.orderResponse.note = response.data.note;
        this.orderResponse.order_date = new Date(
          response.data.order_date[0],
          response.data.order_date[1] - 1,
          response.data.order_date[2]
        );
        this.orderResponse.order_details = response.data.order_details.map(
          (order_detail: OrderDetail) => {
            debugger;
            order_detail.product.thumbnail = `${environment.apiBaseUrl}/products/images/${order_detail.product.thumbnail}`;
            return order_detail;
          }
        );
        this.orderResponse.payment_method = response.data.payment_method;
        this.orderResponse.shipping_method = response.data.shipping_method;

        this.orderResponse.phone_number = response.data.phone_number;
        this.orderResponse.shipping_address = response.data.shipping_address;
        this.orderResponse.shipping_date = new Date(
          response.data.shipping_date[0],
          response.data.shipping_date[1] - 1,
          response.data.shipping_date[2]
        );
        this.orderResponse.status = response.data.status;
        this.orderResponse.total_money = response.data.total_money;
        this.orderResponse.user_id = response.data.user_id;
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        console.log(error);
      },
    });
  }
}
