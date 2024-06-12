import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { Category } from '../../models/category.model';
import { ProductService } from '../../service/product.service';
import { CategoryService } from '../../service/category.service';
import { environment } from '../../environments/environment';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent],
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  currentPage: number = 0;
  itemsPerPage: number = 12;
  pages: number[] = [];
  totalPages: number = 0;
  visiblePages: number[] = [];
  categories: Category[] = [];
  categoryIsSelected: number = 0;
  keyword: string = '';

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private router: Router
  ) {}
  ngOnInit(): void {
    // hàm được gọi ngay khi component được khởi tạo
    this.getProducts(
      this.keyword,
      this.categoryIsSelected,
      this.currentPage,
      this.itemsPerPage
    );
    this.getCategories();
  }

  getProducts(
    keyword: string,
    categoryId: number,
    page: number,
    limit: number
  ) {
    this.productService
      .getProducts(keyword, categoryId, page, limit)
      .subscribe({
        next: (response: any) => {
          debugger;
          response.products.forEach((product: Product) => {
            product.url = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
          });
          this.products = response.products;
          this.totalPages = response.totalPages;
          this.visiblePages = this.generateVisiblePageArray(
            this.currentPage,
            this.totalPages
          );
        },
        complete: () => {
          debugger;
        },
        error: (error) => {
          debugger;
          alert(`Cannot get products, error: ${error.error}`);
        },
      });
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 1);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1)
      .fill(0)
      .map((_, index) => startPage + index);
  }
  searchProducts() {
    this.currentPage = 1;
    this.itemsPerPage = 12;
    debugger;
    this.getProducts(
      this.keyword,
      this.categoryIsSelected,
      this.currentPage,
      this.itemsPerPage
    );
  }

  onProductClick(productId: number) {
    this.router.navigate(['/products/' + productId]);
  }

  onPageChange(page: number) {
    debugger;
    this.currentPage = page;
    this.getProducts(
      this.keyword,
      this.categoryIsSelected,
      this.currentPage,
      this.itemsPerPage
    );
  }

  getCategories() {
    this.categoryService.getAllCategories().subscribe({
      next: (response: any) => {
        debugger;
        this.categories = response.data;
      },
      complete: () => {
        debugger;
      },
      error: (error) => {
        debugger;
        alert(`Cannot get categories, error: ${error.error}`);
      },
    });
  }
}
