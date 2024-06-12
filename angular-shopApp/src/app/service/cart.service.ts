import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private cart: Map<number, number> = new Map<number, number>(); // dùng Map để lưu trữ sản phẩm và số lượng
  localStorage?: Storage;
  constructor(@Inject(DOCUMENT) private document: Document) {
    this.localStorage = document.defaultView?.localStorage;
    // lấy dữ liệu giỏ hàng từ localStorage khi service được khởi tạo
    const storedCart = localStorage.getItem('cart');
    if (storedCart) {
      this.cart = new Map(JSON.parse(storedCart));
    }
  }

  addToCart(productId: number, quantity: number = 1): void {
    debugger;
    // Check product ís exist in cart or not
    if (this.cart.has(productId)) {
      // if exists in cart, increase quantity
      this.cart.set(productId, this.cart.get(productId)! + quantity);
    } else {
      // if not exists in cart, add to cart
      this.cart.set(productId, quantity);
    }
    // after, add to cart
    this.saveCartToLocalStorage();
  }

  getCart(): Map<number, number> {
    return this.cart;
  }

  private saveCartToLocalStorage() {
    localStorage.setItem(
      'cart',
      JSON.stringify(Array.from(this.cart.entries()))
    );
  }
  clearCart() {
    this.cart.clear(); // xóa toàn bộ dữ liệu trong giỏ hàng.
    this.saveCartToLocalStorage(); // lưu lại giỏ hàng mới vào localStorage(trống)
  }
}
