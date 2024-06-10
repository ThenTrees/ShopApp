import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrderDTO } from 'src/app/dtos/order/order.dto';
import { environment } from 'src/app/environments/environment';
import { Product } from 'src/app/models/product.model';
import { CartService } from 'src/app/service/cart.service';
import { OrderService } from 'src/app/service/order.service';
import { ProductService } from 'src/app/service/product.service';
import { TokenService } from 'src/app/service/token.service';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss'],
})
export class OrderComponent implements OnInit {
  orderForm!: FormGroup;
  cartItems: {
    product: Product;
    quantity: number;
  }[] = [];
  couponCode: string = '';
  totalAmount: number = 0;
  orderData: OrderDTO = {
    user_id: 5, // Thay bằng user_id thích hợp
    fullname: '', // Khởi tạo rỗng, sẽ được điền từ form
    email: '', // Khởi tạo rỗng, sẽ được điền từ form
    phone_number: '', // Khởi tạo rỗng, sẽ được điền từ form
    address: '', // Khởi tạo rỗng, sẽ được điền từ form
    note: '', // Có thể thêm trường ghi chú nếu cần
    total_money: 0, // Sẽ được tính toán dựa trên giỏ hàng và mã giảm giá
    payment_method: 'cod', // Mặc định là thanh toán khi nhận hàng (COD)
    shipping_method: 'express', // Mặc định là vận chuyển nhanh (Express)
    coupon_code: '', // Sẽ được điền từ form khi áp dụng mã giảm giá
    cart_items: [],
  };

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private orderService: OrderService,
    private fb: FormBuilder
  ) {
    this.orderForm = this.fb.group({
      fullname: ['Then Trees', Validators.required],
      email: ['thientri.trank17@gmail.com', [Validators.email]],
      phone_number: [
        '0385788328',
        [Validators.required, Validators.minLength(10)],
      ],
      address: ['nha A ngo B', [Validators.required, Validators.minLength(10)]],
      note: ['Hang de vo'],
      shipping_method: ['express', Validators.required],
      payment_method: ['cod', Validators.required],
    });
  }

  ngOnInit(): void {
    debugger;
    if (!localStorage.getItem('access_token')) {
      alert('Vui long dang nhap de mua hang');
      return;
    }
    debugger;
    // lay danh sach san pham tu trong gio hang(localStorage)
    const cart = this.cartService.getCart();
    // lay ra danh sach id cua cac san pham trong gio hang
    const productIds = Array.from(cart.keys());
    debugger;
    // call xuong db de lay thong tin san pham dua tren danh sach id
    this.productService.getProductsByIds(productIds).subscribe({
      next: (response: any) => {
        debugger;
        this.cartItems = response.data.map((productParam: Product) => {
          debugger;
          const product = response.data.find(
            (p: Product) => p.id === productParam.id
          );
          if (product) {
            product.thumbnail = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
          }
          return {
            product: product!,
            quantity: cart.get(+productParam.id)!,
          };
        });
      },
      complete: () => {
        debugger;
        this.calcAmount();
      },
      error: (error) => {
        debugger;
        alert(`Cannot get products, error: ${error.error}`);
      },
    });
  }

  calcAmount(): void {
    this.totalAmount = this.cartItems.reduce(
      (total, item) => total + item.product.price * item.quantity,
      0
    );
  }

  placeOrder(): void {
    debugger;
    // gán giá trị từ form vào đối tượng orderData
    // this.orderData.fullname = this.orderForm.get('fullname')!.value;
    //   this.orderData.email = this.orderForm.get('email')!.value;
    //   this.orderData.phone_number = this.orderForm.get('phone_number')!.value;
    //   this.orderData.address = this.orderForm.get('address')!.value;
    //   this.orderData.note = this.orderForm.get('note')!.value;
    //   this.orderData.shipping_method = this.orderForm.get('shipping_method')!.value;
    //   this.orderData.payment_method = this.orderForm.get('payment_method')!.value;
    //    Sử dụng toán tử spread (...) để sao chép giá trị từ form vào orderData
    if (this.orderForm.valid) {
      this.orderData = {
        ...this.orderData,
        ...this.orderForm.value,
        total_money: this.totalAmount,
      };
      this.orderData.cart_items = this.cartItems.map((cartItem) => ({
        product_id: cartItem.product.id,
        quantity: cartItem.quantity,
      }));
      debugger;
      // Dữ liệu hợp lệ, bạn có thể gửi đơn hàng đi
      this.orderService.placeOrder(this.orderData).subscribe({
        next: (response: any) => {
          debugger;
          alert('Đặt hàng thành công');
          // this.cartService.clearCart();
        },
        complete: () => {
          debugger;
          this.calcAmount();
        },
        error: (err: any) => {
          debugger;
          alert(`Cannot place order, error: ${err.error}`);
        },
      });
    }
  }
}
