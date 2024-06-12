import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'src/app/environments/environment';
import { Product } from 'src/app/models/product.model';
import { ProductImage } from 'src/app/models/productImage.model';
import { CartService } from 'src/app/service/cart.service';
import { ProductService } from 'src/app/service/product.service';

@Component({
  selector: 'app-detail-product',
  templateUrl: './detail-product.component.html',
  styleUrls: ['./detail-product.component.scss'],
})
export class DetailProductComponent implements OnInit {
  product?: Product;
  currentImageIndex: number = 0;
  quantity: number = 1;
  productId: number = 12;
  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}
  ngOnInit(): void {
    // this.cartService.clearCart();
    // Lấy productId từ URL
    const idParam = this.activatedRoute.snapshot.paramMap.get('id')!;
    this.getProduct(parseInt(idParam));
  }

  getProduct(productId: number) {
    if (!isNaN(this.productId)) {
      return this.productService.getProductDetail(productId).subscribe({
        next: (response: any) => {
          debugger;
          // this.product = response.data;
          // Lấy danh sách ảnh sản phẩm và thay đổi URL
          if (
            response.data.productImages &&
            response.data.productImages.length > 0
          ) {
            response.data.productImages.forEach((image: ProductImage) => {
              debugger;
              image.image_url = `${environment.apiBaseUrl}/products/images/${image.image_url}`;
            });
          }
          debugger;
          this.product = response.data;
          this.showImage(0);
        },
        complete: () => {
          debugger;
        },
        error: (error: any) => {
          debugger;
          alert(`Cannot get product, error: ${error.error}`);
        },
      });
    } else {
      debugger;
      alert(`Product id is not valid`);
      return null;
    }
  }

  showImage(index: number): void {
    debugger;
    if (
      this.product &&
      this.product.productImages &&
      this.product.productImages.length > 0
    ) {
      // ddamr bao index nam trong khoang hop le
      let length = this.product.productImages.length - 1;
      if (index < 0) {
        index = length - 1;
      }
      if (index > length) {
        index = 0;
      } else {
        index = index;
      }

      this.currentImageIndex = index; // gan index hien tai va hien thi anh
    }
  }
  thumbnailClick(index: number): void {
    this.currentImageIndex = index; // khi thumbnail duoc click thi lay vi tri cua no lam index
  }

  previousImage() {
    this.showImage(this.currentImageIndex - 1);
  }
  nextImage() {
    this.showImage(this.currentImageIndex + 1);
  }
  decreaseQuantity() {
    this.quantity = this.quantity > 1 ? this.quantity - 1 : 1;
  }
  increaseQuantity() {
    this.quantity = this.quantity >= 1 ? this.quantity + 1 : 1;
  }

  addToCart() {
    debugger;
    if (this.product) {
      alert(`Thêm Thành công ${this.product.name} vào giỏ hàng!`);
      this.cartService.addToCart(this.product.id, this.quantity);
    } else {
      // if product is null, then handle ...
      console.error(`Don't add to cart, because product is null!`);
    }
  }
  buyNow(): void {
    this.addToCart();
    this.router.navigate(['/orders']);
  }

  onInputChange(event: Event): void {
    const inputElement = event.target as HTMLInputElement;
    this.quantity = +inputElement.value;
    console.log('Input value changed:', this.quantity);
  }
}
